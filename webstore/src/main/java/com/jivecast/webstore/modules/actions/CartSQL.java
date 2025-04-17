/*
 *  ------------------------------------------------------------------
 *  JiveCast
 *  301 Fayetteville St Unit 2301
 *  Raleigh, NC 27601
 *  https://jivecast.com
 *
 *  Copyright (c) 2018-2025 JiveCast. All Rights Reserved.
 *
 *  THE PROGRAM IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
 *  LIMITATION, WARRANTIES THAT THE PROGRAM IS FREE OF
 *  DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR
 *  NON-INFRINGING. THE ENTIRE RISK AS TO THE QUALITY AND
 *  PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD ANY PART
 *  OF THE PROGRAM PROVE DEFECTIVE IN ANY RESPECT, YOU
 *  (NOT JIVECAST) ASSUME THE COST OF ANY NECESSARY SERVICING,
 *  REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES
 *  AN ESSENTIAL PART OF THIS LICENSE. NO USE OF
 *  THE PROGRAM IS AUTHORIZED HEREUNDER EXCEPT
 *  UNDER THIS DISCLAIMER.
 *
 *  ------------------------------------------------------------------
 */


package com.jivecast.webstore.modules.actions;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.Item;
import com.jivecast.webstore.om.OrderDetail;
import com.jivecast.webstore.om.WebOrder;
import com.jivecast.webstore.paypal.PaymentServices;
import com.jivecast.webstore.util.email.EmailNotices;
import com.jivecast.webstore.util.tools.CartTool;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;

public class CartSQL extends VelocityAction {

	/** Logging */
	private static Log log = LogFactory.getLog(CartSQL.class);
	private static final int CONFIRMATION_MAX_LENGTH = 16;

	public void doAdd(PipelineData pipelineData, Context context) {

		RunData data = (RunData) pipelineData;

		try {
			// get shopping cart
			CartTool cartTool = (CartTool) context.get("cart");
			int refId = data.getParameters().getInt("refId", -1);
			Item item = Item.getItemByPrimaryKey(refId);
			if (item != null) {
				cartTool.addItem(item, 1);
			}

		} catch (Exception e) {
			log.error("Error adding item to shopping cart: " + e.toString());
		}
	}

	public void doDelete(PipelineData pipelineData, Context context) {

		RunData data = (RunData) pipelineData;

		try {
			// get shopping cart
			CartTool cartTool = (CartTool) context.get("cart");
			int refId = data.getParameters().getInt("refId", -1);
			Item item = Item.getItemByPrimaryKey(refId);
			if (item != null) {
				cartTool.removeItem(item);
			}

		} catch (Exception e) {
			log.error("Error removing item from the shopping cart: " + e.toString());
		}
	}

	public void doEmpty(PipelineData pipelineData, Context context) {

		RunData data = (RunData) pipelineData;

		try {
			// get shopping cart
			CartTool cartTool = (CartTool) context.get("cart");
			cartTool.empty();

		} catch (Exception e) {
			log.error("Error emptying the shopping cart: " + e.toString());
		}
	}

	/**
	 * Convert shopping cart contents into an order to be processed
	 * 
	 * @param pipelineData
	 * @param context
	 */
	public void doOrder(PipelineData pipelineData, Context context) {

		RunData data = (RunData) pipelineData;
		HttpServletRequest request = data.getRequest();

		try {
			CompanyConfig config = CompanyConfig.getDefault();
			if (config != null) {
				// get shopping cart
				CartTool cartTool = (CartTool) context.get("cart");

				// find all the items in the cart
				List<Item> items = cartTool.getItems();

				// create a new weborder
				WebOrder order = new WebOrder();
				order.setOrderDate(new Date());
				order.setShipped(false);
				order.setPaid(false);

				// get a unique confirmation code for this order
				order.setPaypalCustom(EmailNotices.nextConfirmationId(CONFIRMATION_MAX_LENGTH));
				order.setCustomerEmail("");
				order.setCustomerFirstName("");
				order.setCustomerLastName("");

				order.setNew(true);
				order.save();

				// add line item details to the order
				double subtotal = 0.00;
				double tax = 0.00;
				double total = 0.00;
				for (Item item : items) {
					int qty = cartTool.getQuantity(item);
					OrderDetail detail = new OrderDetail();
					detail.setItem(item);
					detail.setItemQuantity(qty);
					detail.setItemPrice(item.getPrice());

					// assign detail to the order
					detail.setWebOrder(order);
					detail.setNew(true);
					detail.save();

					// update subtotal
					subtotal = subtotal + detail.getItemPrice() * qty;
				}

				// update order totals
				tax = subtotal * config.getTaxRate();
				total = subtotal + tax;
				order.setSubtotal(subtotal);
				order.setTax(tax);
				order.setTotal(total);
				order.save();

				try {

					HttpServletResponse response = data.getResponse();
					response.setStatus(HttpServletResponse.SC_OK);

					// create a paypal order and get the approval link to forward our customer to
					PaymentServices paypalService = new PaymentServices();
					paypalService.init();
					String approvalLink = paypalService.authorizePayment(data, order);
					response.sendRedirect(approvalLink);

				} catch (PayPalRESTException ex) {
					context.put("error", true);
					data.setMessage("Error communicating with the paypal server: " + ex.getMessage());
					data.setScreenTemplate("Cart.vm");
					return;
				}

			}

		} catch (Exception e) {
			log.error("Error emptying the shopping cart: " + e.toString());
		}
	}

	/**
	 * Convert shopping cart contents into an order to be processed
	 * 
	 * @param pipelineData
	 * @param context
	 */
	public void doPayment(PipelineData pipelineData, Context context) {

		RunData data = (RunData) pipelineData;
		HttpServletRequest request = data.getRequest();

		try {

			// get shopping cart
			CartTool cartTool = (CartTool) context.get("cart");

			String paymentId = data.getParameters().getString("paymentId", "");
			String payerId = data.getParameters().getString("PayerID", "");
			int refId = data.getParameters().getInt("refId", -1);
			WebOrder order = WebOrder.getWebOrderByPrimaryKey(refId);
			if (order != null) {

				if (!StringUtils.isEmpty(paymentId) && !StringUtils.isEmpty(payerId)) {
					// start payment services
					PaymentServices paymentServices = new PaymentServices();
					paymentServices.init();

					try {

						HttpServletResponse response = data.getResponse();
						response.setStatus(HttpServletResponse.SC_OK);

						Payment payment = paymentServices.executePayment(paymentId, payerId);
						PayerInfo payerInfo = payment.getPayer().getPayerInfo();
						Transaction transaction = payment.getTransactions().get(0);

						// update the order status and deplete inventory
						order.setPaymentCompleted();

						// now we can empty the shopping cart
						cartTool.empty();

						// notify admin that a new order has been completed!
						EmailNotices.notifyAdminPaymentReceived(order);
						
						context.put("payer", payerInfo);
						context.put("order", order);

					} catch (PayPalRESTException ex) {
						context.put("error", true);
						data.setMessage("Error communicating with the paypal server: " + ex.getMessage());
						data.setScreenTemplate("Cart.vm");
						return;
					}

				}
			}

		} catch (Exception e) {
			log.error("Error making payment: " + e.toString());
		}
	}

	/**
	 * Implement this to add information to the context.
	 *
	 * @param data    Turbine information.
	 * @param context Context for web pages.
	 * @exception Exception, a generic exception.
	 */
	public void doPerform(PipelineData pipelineData, Context context) throws Exception {
		RunData data = (RunData) pipelineData;
		User user = data.getUser();
		context.put("user", user);
	}
}

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

package com.jivecast.webstore.modules.screens.paypal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.om.WebOrder;
import com.jivecast.webstore.paypal.PaymentServices;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;

/**
 * Authorize payment with Paypal
 * 
 * @author painter
 *
 */
public class ReviewPayment extends VelocityScreen {

	@Override
	protected void doBuildTemplate(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		try {

			// for the most part, we will let anyone see the news
			HttpServletRequest request = data.getRequest();

			// make sure the response status is always OK
			HttpServletResponse response = data.getResponse();
			response.setStatus(HttpServletResponse.SC_OK);
			String paymentId = request.getParameter("paymentId");
			String payerId = request.getParameter("PayerID");

			try {
				// init the payment services object
				PaymentServices paymentServices = new PaymentServices();
				paymentServices.init();
				Payment payment = paymentServices.getPaymentDetails(paymentId);

				PayerInfo payerInfo = payment.getPayer().getPayerInfo();
				Transaction transaction = payment.getTransactions().get(0);
				ShippingAddress shippingAddress = transaction.getItemList().getShippingAddress();

				request.setAttribute("payer", payerInfo);
				request.setAttribute("transaction", transaction);
				request.setAttribute("shippingAddress", shippingAddress);

				// lookup the weborder
				String paypalCustom = transaction.getCustom();
				WebOrder order = WebOrder.getOrderFromPaypalCustom(paypalCustom);
				
				// update details
				order.setCustomerFirstName( payerInfo.getFirstName() );
				order.setCustomerLastName( payerInfo.getLastName() );
				order.setCustomerEmail( payerInfo.getEmail() );
				
				// set shipping info
				String shipTo = shippingAddress.getRecipientName();
				if ( StringUtils.isEmpty(shipTo)) {
					shipTo = order.getCustomerFirstName() + " " + order.getCustomerLastName();
					shipTo = shipTo.trim();
				}
				order.setRecipientName(shipTo);
				
				order.setAddressLine1(shippingAddress.getLine1());
				order.setAddressLine2(shippingAddress.getLine2());
				order.setCity(shippingAddress.getCity());
				order.setState(shippingAddress.getState());
				order.setZip(shippingAddress.getPostalCode());
				order.setCountry(shippingAddress.getCountryCode());
				
				order.save();
				
				// populate the context
				context.put("order",  order);
				context.put("paymentId",  paymentId);
				context.put("PayerID",  payerId);
				return;

			} catch (PayPalRESTException ex) {
				request.setAttribute("errorMessage", ex.getMessage());
				ex.printStackTrace();
				request.getRequestDispatcher("error.jsp").forward(request, response);
			}

		} catch (Exception e) {
			log.error("Error getting item categories: " + e.toString());
		}
	}
}

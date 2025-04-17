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

package com.jivecast.webstore.paypal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.util.RunData;

import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.OrderDetail;
import com.jivecast.webstore.om.WebOrder;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public class PaymentServices {

	// create an instance of the logging facility
	private static Log log = LogFactory.getLog(PaymentServices.class);

	private String CLIENT_ID = "Your_PayPal_Client_ID";
	private String CLIENT_SECRET = "Your_PayPal_Client_Secret";
	private String SERVER_URL = "https://yourserver.com/webstore/"; // getServerURL(data);
	
	private String MODE = "sandbox";

	/**
	 * Setup the paypal credentials
	 */
	public void init() {
		try {
			CompanyConfig config = CompanyConfig.getDefault();
			this.CLIENT_ID = config.getPaypalClientId();
			this.CLIENT_SECRET = config.getPaypalClientSecret();
			if (config.isPaypalSandbox() == true) {
				this.MODE = "sandbox";
			} else {
				this.MODE = "live";
			}

		} catch (Exception e) {
			log.error("Could not load PayPal credentials: " + e.toString());
		}
	}

	public String authorizePayment(RunData data, WebOrder order) throws PayPalRESTException {
		// get payer info
		Payer payer = getPayerInformation(order);
		RedirectUrls redirectUrls = getRedirectURLs(data);
		List<Transaction> listTransaction = getTransactionInformation(order);

		Payment requestPayment = new Payment();
		requestPayment.setTransactions(listTransaction);
		requestPayment.setRedirectUrls(redirectUrls);

		// what if we don't know the payer yet?
		requestPayment.setPayer(payer);
		requestPayment.setIntent("authorize");

		APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);
		Payment approvedPayment = requestPayment.create(apiContext);
		return getApprovalLink(approvedPayment);
	}

	private Payer getPayerInformation(WebOrder order) {

		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		PayerInfo payerInfo = new PayerInfo();
		payerInfo.setFirstName(order.getCustomerFirstName());
		payerInfo.setLastName(order.getCustomerLastName());
		payerInfo.setEmail(order.getCustomerEmail());
		payer.setPayerInfo(payerInfo);
		return payer;
	}

	private RedirectUrls getRedirectURLs(RunData data) {
		RedirectUrls redirectUrls = new RedirectUrls();
		String server_url = SERVER_URL;
		String confirm_url = server_url + "/app/template/paypal,Cancel.vm";
		String return_url = server_url + "/app/template/paypal,ReviewPayment.vm";

		redirectUrls.setCancelUrl(confirm_url);
		redirectUrls.setReturnUrl(return_url);
		return redirectUrls;
	}

	private static String getServerURL(RunData data) {
		try {
			String server_name = Turbine.getServerName();
			String server_port = Turbine.getServerPort();
			String server_context = Turbine.getContextPath();
			String protocol = "http://";
			if (data.getRequest().isSecure())
				protocol = "https://";

			log.debug("ServerName: " + server_name + " Port: " + server_port + " Context: " + server_context
					+ " Proto: " + protocol);

			// initial server url
			String server_url = protocol + server_name + ":" + server_port + server_context;

			// Remove port info for https protocol
			if (protocol.equals("https://") || server_port.equals("443"))
				server_url = protocol + server_name + server_context;
			else {
				// if standard ports, remove that as well
				if (server_port.equals("80"))
					server_url = protocol + server_name + server_context;
			}

			return server_url;
		} catch (Exception e) {
			return null;
		}
	}

	private List<Transaction> getTransactionInformation(WebOrder order) {

		try {
			// get global config
			CompanyConfig config = CompanyConfig.getDefault();

			// get the list of our order details
			List<OrderDetail> orderDetails = order.getDetails();

			// money formatter
			DecimalFormat df = new DecimalFormat("0.00");

			Details details = new Details();
			details.setShipping(df.format(order.getShipping()));
			details.setSubtotal(df.format(order.getSubtotal()));
			details.setTax(df.format(order.getTax()));
			details.setShipping(df.format(order.getShipping()));

			Amount amount = new Amount();
			amount.setCurrency("USD");
			amount.setTotal(df.format(order.getTotal()));
			amount.setDetails(details);

			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			transaction.setDescription(config.getCompanyName() + " web order");

			// this is how we link the payment back to our order in the database
			transaction.setCustom(order.getPaypalCustom());

			ItemList itemList = new ItemList();

			// add each item to the items set for processing
			List<Item> items = new ArrayList<>();
			for (OrderDetail detail : orderDetails) {
				Item item = new Item();
				item.setCurrency("USD");
				item.setName(detail.getItem().getTitle());

				// compute line item price
				double price = detail.getItemPrice() * detail.getItemQuantity();
				item.setPrice(df.format(price));

				item.setTax("");
				item.setQuantity(new Integer(detail.getItemQuantity()).toString());
				items.add(item);
			}

			itemList.setItems(items);
			transaction.setItemList(itemList);

			List<Transaction> listTransaction = new ArrayList<>();
			listTransaction.add(transaction);

			return listTransaction;
		} catch (Exception e) {
			log.error("Unable to create transaction: " + e.toString());
			return null;
		}

	}

	private String getApprovalLink(Payment approvedPayment) {
		List<Links> links = approvedPayment.getLinks();
		String approvalLink = null;

		for (Links link : links) {
			if (link.getRel().equalsIgnoreCase("approval_url")) {
				approvalLink = link.getHref();
				break;
			}
		}

		return approvalLink;
	}

	public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);
		return Payment.get(apiContext, paymentId);
	}

	/**
	 * Execute the final payment
	 * 
	 * @param paymentId
	 * @param payerId
	 * @return
	 * @throws PayPalRESTException
	 */
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payerId);
		Payment payment = new Payment().setId(paymentId);
		APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);
		return payment.execute(apiContext, paymentExecution);
	}

}

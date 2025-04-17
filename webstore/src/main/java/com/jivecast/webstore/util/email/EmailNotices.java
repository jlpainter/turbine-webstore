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

package com.jivecast.webstore.util.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.HtmlEmail;
import org.apache.turbine.Turbine;
import org.apache.turbine.util.RunData;

import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.WebOrder;

/**
 * Email helper class for sending user notifications
 * 
 * @author painter
 *
 */
public class EmailNotices {

	private static Log log = LogFactory.getLog(EmailNotices.class);

	private static String EMAIL_CONFIRMATION_TEMPLATE = "/email-confirmation.html";
	private static String NEW_USER_CONFIRMATION_TEMPLATE = "/admin-new-user-confirmation.html";
	private static String EMAIL_CHANGE_CONFIRMATION_TEMPLATE = "/change-email-confirmation.html";
	private static String EMAIL_PAYMENT_RECEIEVED_TEMPLATE = "/payment-confirmation.html";
	private static String ADMIN_PAYMENT_RECEIEVED_TEMPLATE = "/admin-payment-confirmation.html";
	private static String PASSWORD_RESET_TEMPLATE = "/password-reset.html";
	private static String DEFAULT_FROM_EMAIL = "no-reply@mycompany.com";
	private static String DEFAULT_FROM_NAME = "MyCompany";

	/**
	 * Ask turbine for the real path to our email templates
	 * 
	 * @return
	 */
	public static String getEmailTemplatePath() {
		try {
			String path = Turbine.getRealPath("/");
			return path + "/data/email-templates/";
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Create random confirmation id's of 16 characters in length
	 * 
	 * @return
	 */
	public static String nextConfirmationId(int length) {
		SecureRandom random = new SecureRandom();
		String x = new BigInteger(130, random).toString(32);
		return x.substring(0, length - 1);
	}

	public static String getServerURL(RunData data) {
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

	public static String getPasswordResetURL(RunData data, String confirmId) {
		String server_url = getServerURL(data);
		if (server_url != null) {
			String confirm_url = server_url + "/app/template/confirm,Password.vm/uid/" + confirmId;
			return confirm_url;
		} else {
			return "";
		}
	}

	public static String getEmailConfirmationURL(RunData data, String confirmId) {
		String server_url = getServerURL(data);
		if (server_url != null) {
			String confirm_url = server_url + "/app/template/confirm,ValidateEmail.vm/uid/" + confirmId;
			return confirm_url;
		} else {
			return "";
		}
	}

	public static String getChangeEmailConfirmationURL(RunData data, String confirmId) {
		String server_url = getServerURL(data);
		if (server_url != null) {
			String confirm_url = server_url + "/app/template/confirm,ChangeEmail.vm/uid/" + confirmId;
			return confirm_url;
		} else {
			return "";
		}
	}

	/**
	 * Used to confirm a user's identity by sending them an email confirmation
	 * 
	 * @param data
	 * @param username
	 * @param confirm_url
	 */
	public static void sendChangeEmailConfirmation(RunData data, String username, String confirm_url) {
		try {
			// locate the mail server
			String smtp_host = Turbine.getConfiguration().getString("mail.server");

			// Email composition
			HtmlEmail email = new HtmlEmail();
			email.setHostName(smtp_host);
			email.setSmtpPort(25);

			email.addTo(username);
			email.setFrom(DEFAULT_FROM_EMAIL, DEFAULT_FROM_NAME);
			email.setSubject(DEFAULT_FROM_NAME + " - Change of Email Confirmation");
			email.setSentDate(new Date());

			// Compose the message output from our template
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(
					new FileReader(getEmailTemplatePath() + EMAIL_CHANGE_CONFIRMATION_TEMPLATE));

			String line;
			while ((line = br.readLine()) != null) {

				// perform substitutions in the template
				if (line.contains("$username") == true)
					line = line.replace("$username", username);

				if (line.contains("$confirm_url") == true)
					line = line.replace("$confirm_url", confirm_url);

				sb.append(line);
			}

			email.setHtmlMsg(sb.toString());
			email.send();

		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("Error sending change email confirmation: ");
			sb.append(e.toString());
			data.setMessage(sb.toString());
			log.error(sb.toString());
		}
	}

	/**
	 * Used to confirm a user's identity by sending them an email confirmation
	 * 
	 * @param data
	 * @param username
	 * @param confirm_url
	 */
	public static void sendEmailConfirmation(RunData data, String username, String confirm_url) {
		try {

			// locate the mail server
			String smtp_host = Turbine.getConfiguration().getString("mail.server");

			// Email composition
			HtmlEmail email = new HtmlEmail();
			email.setHostName(smtp_host);
			email.setSmtpPort(25);

			email.addTo(username);
			email.setFrom(DEFAULT_FROM_EMAIL, DEFAULT_FROM_NAME);
			email.setSubject(DEFAULT_FROM_NAME + " - Email Confirmation");
			email.setSentDate(new Date());

			// Compose the message output from our template
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(
					new FileReader(getEmailTemplatePath() + EMAIL_CONFIRMATION_TEMPLATE));

			String line;
			while ((line = br.readLine()) != null) {

				// perform substitutions in the template
				if (line.contains("$username") == true)
					line = line.replace("$username", username);

				if (line.contains("$confirm_url") == true)
					line = line.replace("$confirm_url", confirm_url);

				sb.append(line);
			}

			email.setHtmlMsg(sb.toString());
			email.send();

		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("Error sending email confirmation: ");
			sb.append(e.toString());
			data.setMessage(sb.toString());
			log.error(sb.toString());
		}
	}

	public static void sendPasswordReset(RunData data, String username, String confirm_url) {
		try {

			// locate the mail server
			String smtp_host = Turbine.getConfiguration().getString("mail.server");

			// Email composition
			HtmlEmail email = new HtmlEmail();
			email.setHostName(smtp_host);
			email.setSmtpPort(25);

			email.addTo(username);
			email.setFrom(DEFAULT_FROM_EMAIL, DEFAULT_FROM_NAME);
			email.setSubject(DEFAULT_FROM_NAME + " - Password Reset");
			email.setSentDate(new Date());

			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(getEmailTemplatePath() + PASSWORD_RESET_TEMPLATE));

			String line;
			while ((line = br.readLine()) != null) {

				// perform substitutions in the template
				if (line.contains("$username") == true)
					line = line.replace("$username", username);

				if (line.contains("$confirm_url") == true)
					line = line.replace("$confirm_url", confirm_url);

				sb.append(line);
			}

			email.setHtmlMsg(sb.toString());
			email.send();

		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("Error sending password reset email: ");
			sb.append(e.toString());
			data.setMessage(sb.toString());
			log.error(sb.toString());
		}

	}

	public static void notifyAdminPaymentReceived(WebOrder order) {
		try {

			// get system config for billing
			CompanyConfig config = CompanyConfig.getDefault();

			// locate the mail server
			String smtp_host = Turbine.getConfiguration().getString("mail.server");

			// Email composition
			HtmlEmail email = new HtmlEmail();
			email.setHostName(smtp_host);
			email.setSmtpPort(25);

			email.addTo(config.getAdminEmail());
			email.setFrom(DEFAULT_FROM_EMAIL, DEFAULT_FROM_NAME);
			email.setSubject(DEFAULT_FROM_NAME + " - Payment Received!");
			email.setSentDate(new Date());

			// Compose the message output from our template
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(
					new FileReader(getEmailTemplatePath() + ADMIN_PAYMENT_RECEIEVED_TEMPLATE));

			String line;
			while ((line = br.readLine()) != null) {
				// perform substitutions in the template
				if (line.contains("$order_number") == true)
					line = line.replace("$order_number", new Integer(order.getRefId()).toString());
				sb.append(line);
			}

			email.setHtmlMsg(sb.toString());
			email.send();

		} catch (Exception e) {
			log.error("Error sending notification to admin: " + e.toString());
		}
	}

}

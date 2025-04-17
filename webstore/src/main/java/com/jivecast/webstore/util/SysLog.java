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

package com.jivecast.webstore.util;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.util.RunData;

import com.jivecast.webstore.om.EventLog;
import com.jivecast.webstore.wrapper.TurbineUserWrapper;

/**
 * Create standard system loggin interface
 * 
 * @author painter
 *
 */
public class SysLog {

	/** Logging */
	private static Log log = LogFactory.getLog(SysLog.class);

	/** Transaction Types **/

	// user actions
	public static final int ADD_USER = 5;
	public static final int UPDATE_USER = 6;
	public static final int DELETE_USER = 7;
	public static final int LOGIN_SUCCESS = 11;
	public static final int LOGIN_FAILURE = 12;
	public static final int ACCOUNT_DISABLED = 13;
	public static final int USER_PASSWORD_CHANGE = 14;

	// system actions
	public static final int UPDATE_COMPANY_DETAIL = 30;
	public static final int ADD_COMPANY_LOGO = 31;
	public static final int UPDATE_COMPANY_TERMS = 32;
	public static final int UPDATE_COMPANY_PAYPAL = 33;

	// inventory actions
	public static final int ITEM_ADDED = 50;
	public static final int ITEM_UPDATED = 51;
	public static final int ITEM_DELETED = 52;

	public static final int ITEM_CAT_ADDED = 50;
	public static final int ITEM_CAT_UPDATED = 51;
	public static final int ITEM_CAT_DELETED = 52;

	// paypal
	public static final int PAYPAL_RECEIVED = 200;
	public static final int PAYPAL_REVOKE = 201;
	public static final int PAYPAL_ERROR = 210;
	public static final int PRINT_INVOICE = 220;

	//
	public static final int PASSWORD_RESET_REQUEST = 300;
	public static final int PASSWORD_RESET_BAD_LINK = 301;
	public static final int PASSWORD_RESET_SUCCESS = 302;

	public static final int REGISTER_ACT_REQUEST = 310;

	/**
	 * Standard logging interface for smarttext event monitoring
	 * 
	 * @param data
	 * @param txType
	 * @param msg
	 */
	public static void log(RunData data, int txType, String msg) {

		try {
			// get the insight user to map to the event log
			TurbineUserWrapper tu = data.getUser();

			// get the client IP address
			String ip = data.getRequest().getRemoteAddr();

			// Log the event
			EventLog txLog = new EventLog();
			txLog.setIpAddress(ip);
			txLog.setTransactionType(txType);
			txLog.setMessage(msg);
			txLog.setTxDate(new Date());
			txLog.setNew(true);
			txLog.save();

		} catch (Exception e) {
			log.error("Could not add system event log: " + e.toString());
		}
	}
}

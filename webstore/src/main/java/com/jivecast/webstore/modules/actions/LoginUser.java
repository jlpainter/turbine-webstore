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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.FulcrumSecurityException;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;

import com.jivecast.webstore.om.EventLog;
import com.jivecast.webstore.util.SysLog;
import com.jivecast.webstore.wrapper.TurbineUserWrapper;

/**
 * This is where we authenticate the user logging into the system against a user
 * in the database. If the user exists in the database that users last login
 * time will be updated.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class LoginUser extends org.apache.turbine.modules.actions.LoginUser {


	/** CGI Parameter for the user name */
	public static final String CGI_USERNAME = "username";

	/** CGI Parameter for the password */
	public static final String CGI_PASSWORD = "password";

	/** Logging */
	private static Log log = LogFactory.getLog(LoginUser.class);

	/** Injected service instance */
	@TurbineService
	private SecurityService security;

	@TurbineConfiguration(TurbineConstants.LOGIN_ERROR)
	private String loginError = "";

	@TurbineConfiguration(TurbineConstants.TEMPLATE_LOGIN)
	private String templateLogin;

	@TurbineConfiguration(TurbineConstants.SCREEN_LOGIN)
	private String screenLogin;	
	

	/**
	 * Checks for anonymous user, else calls parent method.
	 *
	 * @param pipelineData Turbine information.
	 * @exception FulcrumSecurityException could not get instance of the anonymous
	 *                                     user
	 */
	@Override
	public void doPerform(PipelineData pipelineData) throws FulcrumSecurityException {
		
		RunData data = (RunData) pipelineData;

		// make this variable accessible to the exception handler
		String username = data.getParameters().getString(CGI_USERNAME, "");
		String password = data.getParameters().getString(CGI_PASSWORD, "");

		if (StringUtils.isEmpty(username)) {
			reset(data, "Enter your username");
			return;
		}

		if (StringUtils.isEmpty(password)) {
			reset(data, "Enter your password");
			return;
		}

		if (username.equals(security.getAnonymousUser().getName())) {
			
			( (RunData) data).setMessage("Anonymous user cannot login");
			reset(data, "");
			return;
		}


		try {
			
			// Authenticate the user and get the object.
			TurbineUserWrapper user = security.getAuthenticatedUser(username, password);			

			// Store the user object.
			data.setUser(user);

			// Mark the user as being logged in.
			user.setHasLoggedIn(Boolean.TRUE);

			// Set the last_login date in the database.
			user.updateLastLogin();

			// This only happens if the user is valid; otherwise, we
			// will get a valueBound in the User object when we don't
			// want to because the username is not set yet. Save the
			// User object into the session.
			data.save();

			// record the login
			SysLog.log(data, SysLog.LOGIN_SUCCESS, "Logged in!");

			// redirect to summary screen
			data.setScreenTemplate("admin,Config.vm");
			return;

		} catch (PasswordMismatchException pme) {

			// invalid password, do some logging an increase the login attempt counter
			if (!StringUtils.isEmpty(username)) {
				if (security.accountExists(username)) {
					TurbineUserWrapper user = security.getUser(username);
					
					// record the login attempt
					logAuthenticationError(username);
				}
			}

			// redirect failed login attempt
			data.setMessage("Login failed");
			reset(data, "Login failed");

		} catch (Exception e) {
			if (e instanceof DataBackendException) {
				log.error(e);
			}
			reset(data, "Login failed");
		}
	}


	private void reset(RunData data, String msg) throws UnknownEntityException {

		// Set message if provided
		if (!StringUtils.isEmpty(msg))
			data.setMessage(msg);

		User anonymousUser = security.getAnonymousUser();
		data.setUser(anonymousUser);

		if (StringUtils.isNotEmpty(templateLogin)) {
			// We're running in a templating solution
			data.setScreenTemplate(templateLogin);
		} else {
			data.setScreen(screenLogin);
		}
	}


	/**
	 * Log authentication errors
	 *
	 * @param username
	 * @throws DataBackendException
	 */
	private void logAuthenticationError(String username) {
		try {
			TurbineUserWrapper user = security.getUser(username);

			EventLog t_log = new EventLog();
			t_log.setUserId((int) user.getId());
			t_log.setTransactionType(SysLog.LOGIN_FAILURE);
			t_log.setMessage("denied login for: " + username);
			t_log.setTxDate(new Date());
			t_log.setNew(true);
			t_log.save();
		} catch (Exception e) {
			log.error(e);
		}
	}
}

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

package com.jivecast.webstore.modules.screens;



import org.apache.fulcrum.security.model.turbine.TurbineAccessControlList;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.screens.VelocitySecureScreen;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.commons.configuration2.Configuration;

/**
 * This class provides a sample implementation for creating a secured screen
 */
public class SecureScreen extends VelocitySecureScreen {
	@TurbineService
	protected SecurityService securityService;

	@TurbineConfiguration(TurbineConstants.TEMPLATE_LOGIN)
	private Configuration templateLogin;

	@TurbineConfiguration(TurbineConstants.TEMPLATE_HOMEPAGE)
	private Configuration templateHomepage;

	@Override
	protected boolean isAuthorized(PipelineData pipelineData) throws Exception {
		boolean isAuthorized = false;
		RunData data = (RunData) pipelineData;

		// Who is our current user?
		User user = data.getUser();

		// Get the Turbine ACL implementation
		TurbineAccessControlList acl = data.getACL();

		if (acl == null) {
			// commons configuration getProperty: prefix removed, the key for the value ..
			// is an empty string, the result an object
			data.setScreenTemplate((String) templateLogin.getProperty(""));
			isAuthorized = false;
		} else if (acl.hasRole("turbineuser") || acl.hasRole("turbineadmin")) {
			isAuthorized = true;
		} else {
			data.setScreenTemplate((String) templateHomepage.getProperty(""));
			data.setMessage("You do not have access to this part of the site.");
			isAuthorized = false;
		}
		return isAuthorized;
	}

	/**
	 * Implement this to add information to the context.
	 *
	 * @param data    Turbine information.
	 * @param context Context for web pages.
	 * @exception Exception, a generic exception.
	 */
	@Override
	protected void doBuildTemplate(PipelineData data, Context context) throws Exception {

	}
}

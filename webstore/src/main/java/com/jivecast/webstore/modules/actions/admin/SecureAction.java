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

package com.jivecast.webstore.modules.actions.admin;

import org.apache.fulcrum.localization.LocalizationService;
import org.apache.fulcrum.security.model.turbine.TurbineAccessControlList;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.actions.VelocitySecureAction;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Velocity Secure action.
 *
 * Always performs a Security Check that you've defined before executing the
 * doPerform().
 */
public class SecureAction extends VelocitySecureAction {

	@TurbineService
	private LocalizationService localizationService;

	/**
	 * This currently only checks to make sure that user is allowed to view the
	 * storage area. If you create an action that requires more security then
	 * override this method.
	 *
	 * @param data Turbine information.
	 * @return True if the user is authorized to access the screen.
	 * @exception Exception, a generic exception.
	 */
	@Override
	protected boolean isAuthorized(PipelineData pipelineData) throws Exception {
		boolean isAuthorized = false;
		RunData data = (RunData) pipelineData;

		// Who is our current user?
		User user = ((RunData) data).getUser();

		// Get the Turbine ACL implementation
		TurbineAccessControlList acl = data.getACL();

		if (acl == null || !(acl.hasRole("turbineadmin") || !acl.hasRole("turbineuser"))) {
			String msg = localizationService.getString(localizationService.getDefaultBundleName(),
					localizationService.getLocale(((RunData) data).getRequest()), "no_permission");

			data.setMessage(msg);

			log.debug(
					"call not permitted for template: " + data.getScreenTemplate() + " and action " + data.getAction());
			data.setScreenTemplate("Login.vm");
			isAuthorized = false;
		} else if (acl.hasRole("turbineadmin") || acl.hasRole("turbineuser")) {
			isAuthorized = true;
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
	public void doPerform(PipelineData pipelineData, Context context) throws Exception {
		RunData data = (RunData) pipelineData;
		User user = data.getUser();
		context.put("user", user);
	}

}

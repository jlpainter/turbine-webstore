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



import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Change Password action.
 *
 */
public class ChangePasswordAction extends SecureAction
{

	/** Injected service instance */
	@TurbineService
	private SecurityService security;

	/**
	 * Implement this to add information to the context.
	 *
	 * @param data    Turbine information.
	 * @param context Context for web pages.
	 * @exception Exception, a generic exception.
	 */
	@Override
	public void doPerform(PipelineData pipelineData) throws Exception 
	{
		RunData data = (RunData) pipelineData;
		User user = data.getUser();

		String oldPassword = data.getParameters().getString("oldpassword", "");
		String newPassword = data.getParameters().getString("newpassword", "");

		try {
			security.changePassword(user, oldPassword, newPassword);
			data.setMessage("Password changed!");
		} catch (PasswordMismatchException e) {
			data.setMessage(e.getMessage());
			data.setScreenTemplate("Password.vm");
		}

	}

	/**
	 * Implement this to add information to the context.
	 *
	 * @param data    Turbine information.
	 * @param context Context for web pages.
	 * @exception Exception, a generic exception.
	 */
	@Override
	public void doPerform(PipelineData data, Context context) throws Exception 
	{
		context.put("success", "Password changed!!");
	}

}

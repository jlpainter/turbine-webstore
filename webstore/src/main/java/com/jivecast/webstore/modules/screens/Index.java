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

import org.apache.turbine.modules.screens.VelocitySecureScreen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.ItemCategory;

/**
 * This class provides the data required for displaying content in the Velocity
 * page.
 */
public class Index extends VelocitySecureScreen {

	/**
	 * This method is called by the Turbine framework when the associated Velocity
	 * template, Index.vm is requested
	 * 
	 * @param data    the Turbine request data
	 * @param context the Velocity context
	 * @throws Exception a generic Exception
	 */
	@Override
	protected void doBuildTemplate(PipelineData pipelineData, Context context) throws Exception {
		RunData data = (RunData) pipelineData;
	}

	/**
	 * This method is called bythe Turbine framework to determine if the current
	 * user is allowed to use this screen. If this method returns false, the
	 * doBuildTemplate() method will not be called. If a redirect to some "access
	 * denied" page is required, do the necessary redirect here.
	 * 
	 * return always <code>true</code>true, to show this screen as default
	 */
	@Override
	protected boolean isAuthorized(PipelineData pipelineData) throws Exception {
		// use data.getACL()
		return true;
	}
}

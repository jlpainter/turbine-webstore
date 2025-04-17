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

package com.jivecast.webstore.modules.screens.admin;


import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.om.WebOrder;

public class ViewOrders extends SecureScreen {

	@Override
	protected void doBuildTemplate(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;
		try {
		
			String mode = data.getParameters().getString("mode", "");

			// add all orders that are complete and need to be shipped
			if (StringUtils.isEmpty(mode))
				context.put("entries",  WebOrder.getUnshippedOrders() );
			else {
				
				if ( mode.contentEquals("completed") )
					context.put("entries",  WebOrder.getCompletedOrders() );

				if ( mode.contentEquals("incomplete") )
					context.put("entries",  WebOrder.getIncompletedOrders() );
				
			}
			

		} catch (Exception e) {
			log.error("Error getting messages: " + e.toString());
		}
	}

}

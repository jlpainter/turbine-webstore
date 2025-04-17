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

package com.jivecast.webstore.modules.screens.inventory;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.om.Item;
import com.jivecast.webstore.om.ItemCategory;

public class Items extends SecureScreen {

	@Override
	protected void doBuildTemplate(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		try {

			// add all categories by default
			context.put("categories", ItemCategory.getAllCategories());

			// Get all items
			context.put("items", Item.getAllItems());
			
			// did user request a particular category to edit?
			int refId = data.getParameters().getInt("refId", -1);
			Item item = Item.getItemByPrimaryKey(refId);
			if (item != null) {
				context.put("entry", item);
			}

		} catch (Exception e) {
			log.error("Error getting item categories: " + e.toString());
		}
	}

}

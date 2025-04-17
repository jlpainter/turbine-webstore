package com.jivecast.webstore.modules.actions.inventory;

/*
 *  ------------------------------------------------------------------
 *  Jeffery L Painter, <jeff@jivecast.com>
 *
 *  Copyright (c) 2020 JiveCast
 *  All Rights Reserved.
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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.modules.actions.CartSQL;
import com.jivecast.webstore.om.ItemCategory;
import com.jivecast.webstore.om.ItemCategoryPeer;

/**
 * @author painter
 */
public class CategorySQL extends SecureAction {

    /** Logging */
    private static Log log = LogFactory.getLog(CategorySQL.class);
	
	private static final String REF_ID = "refId";

	public void doInsert(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;
		try {
			String categoryName = data.getParameters().getString("category");
			if (!StringUtils.isEmpty(categoryName)) {
				ItemCategory entry = new ItemCategory();
				entry.setCategoryName(categoryName);
				entry.setNew(true);
				entry.save();

				data.setMessage("New category: " + categoryName + " added!");
			} else {
				// context.put("error", true);
				data.setMessage("Must provide a category name");
			}

		} catch (Exception e) {
			// context.put("error", true);
			data.setMessage("Error adding new group: " + e.toString());
		}
	}

	public void doDelete(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		int refId = data.getParameters().getInt(REF_ID, -1);
		ItemCategory entry = ItemCategory.getItemCategoryByPrimaryKey(refId);
		if (entry != null) {

			List<com.jivecast.webstore.om.Item> items = entry.getItems();
			if (items.size() == 0) {
				ItemCategoryPeer.doDelete(entry);
				data.setMessage("Category removed");
			} else {
				context.put("error", true);
				data.setMessage(
						"This cateogry is currently assigned to existing items. They must be re-assigned before you can remove the category label.");
			}

		} else {
			context.put("error", true);
			data.setMessage("Could not find category");
		}
	}

	public void doUpdate(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		int refId = data.getParameters().getInt(REF_ID, -1);
		ItemCategory entry = ItemCategory.getItemCategoryByPrimaryKey(refId);

		if (entry != null) {
			String categoryName = data.getParameters().getString("category");
			if (!StringUtils.isEmpty(categoryName)) {
				entry.setCategoryName(categoryName);
				entry.save();
				data.setMessage("Renamed category successfully");
				return;
			} else {
				context.put("error", true);
				data.setMessage("Category name cannot be empty");
				return;
			}
		}

	}

	/**
	 * This is used in the event that the doInsert above fails.
	 */
	public void doPerform(PipelineData pipelineData, Context context) throws Exception {
		RunData data = (RunData) pipelineData;
		context.put("error", true);
		data.setMessage("Invalid button!");
	}

}

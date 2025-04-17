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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.om.OrderDetail;
import com.jivecast.webstore.om.OrderDetailPeer;
import com.jivecast.webstore.om.WebOrder;
import com.jivecast.webstore.om.WebOrderPeer;

/**
 * @author painter
 */
public class OrderAction extends SecureAction {

	/** Logging */
	private static Log log = LogFactory.getLog(ConfigSQL.class);

	public void doShip(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		try {

			int refId = data.getParameters().getInt("refId", -1);
			WebOrder order = WebOrder.getWebOrderByPrimaryKey(refId);
			if (order != null) {

				// update shipped status
				order.setShipped(true);
				order.save();
				data.setMessage("Order " + order.getRefId() + " has been marked shipped.");
				return;
			} else {
				context.put("error", true);
				data.setMessage("Order not found, shipped status not changed.");
				return;
			}
		} catch (Exception e) {
			context.put("error", true);
			data.setMessage("Order not found, shipped status not changed.");
			return;

		}

	}


	/**
	 * Allow user to undo a shipped order if clicked by mistake
	 * @param pipelineData
	 * @param context
	 * @throws Exception
	 */
	public void doUnship(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		try {
			int refId = data.getParameters().getInt("refId", -1);
			WebOrder order = WebOrder.getWebOrderByPrimaryKey(refId);
			if (order != null) {

				// update shipped status
				order.setShipped(false);
				order.save();
				data.setMessage("Order " + order.getRefId() + " has reverted to NOT shipped.");
				return;
			} else {
				context.put("error", true);
				data.setMessage("Order not found, shipped status not changed.");
				return;
			}
		} catch (Exception e) {
			context.put("error", true);
			data.setMessage("Order not found, shipped status not changed.");
			return;

		}

	}

	
	public void doCancel(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		try {

			int refId = data.getParameters().getInt("refId", -1);
			WebOrder order = WebOrder.getWebOrderByPrimaryKey(refId);
			if (order != null) {

				List<OrderDetail> details = order.getDetails();
				for ( OrderDetail detail : details )
				{
					OrderDetailPeer.doDelete(detail);
				}
				
				WebOrderPeer.doDelete(order);
				
				data.setMessage("Order " + refId + " has been removed");
				return;
			} else {
				context.put("error", true);
				data.setMessage("Order not found.");
				return;
			}
		} catch (Exception e) {
			context.put("error", true);
			data.setMessage("Order not found.");
			return;

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

	private static void createDirectory(String dir) throws IOException {
		if (!directoryExists(dir)) {
			Path path = Paths.get(dir);
			Files.createDirectories(path);
		}
	}

	private static boolean directoryExists(String dir) {
		Path path = Paths.get(dir);
		if (Files.exists(path, LinkOption.NOFOLLOW_LINKS))
			return true;
		return false;
	}

	private static void deleteDirectory(String dir) {
		Path path = Paths.get(dir);
		try {
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

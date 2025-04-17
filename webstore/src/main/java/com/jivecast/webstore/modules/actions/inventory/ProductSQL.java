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

package com.jivecast.webstore.modules.actions.inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.jivecast.webstore.om.Item;
import com.jivecast.webstore.om.ItemCategory;
import com.jivecast.webstore.om.ItemPeer;
import com.jivecast.webstore.util.SysLog;

/**
 * @author painter
 */
public class ProductSQL extends SecureAction {

	/** Logging */
	private static Log log = LogFactory.getLog(ProductSQL.class);

	private static final String REF_ID = "refId";
	private static final String UPLOAD_PATH = "/images/uploads/";

	/**
	 * Add new item
	 *
	 * @param data    Turbine information.
	 * @param context Context for web pages.
	 */
	public void doInsert(PipelineData pipelineData, Context context) {
		RunData data = (RunData) pipelineData;
		try {

			int catId = data.getParameters().getInt("catId", -1);
			ItemCategory category = ItemCategory.getItemCategoryByPrimaryKey(catId);
			if (category != null) {
				String title = data.getParameters().getString("title", "");
				String desc = data.getParameters().getString("description", "");
				double price = data.getParameters().getDouble("price", 0.00);
				int quantity = data.getParameters().getInt("quantity", 0);

				if (!StringUtils.isEmpty(title)) {
					Item item = new Item();
					item.setTitle(title);
					item.setDescription(desc);
					item.setQuantity(quantity);
					item.setPrice(price);

					// TODO - make this an option user can change
					item.setShowSold(true);

					// assign to category
					item.setItemCategory(category);
					item.setNew(true);
					item.save();

					// upload the study file
					Part fileItem = data.getParameters().getPart("imageFile");
					if (fileItem != null) {
						String fileName = fileItem.getSubmittedFileName();
						String[] f = fileName.split("\\.");
						if (f.length > 1) {
							String extension = f[f.length - 1];

							// create the output directory
							String imagePath = Turbine.getRealPath("/") + UPLOAD_PATH;

							// create the directory if it does not exist
							createDirectory(imagePath);

							// assign file name and save
							String imageName = item.getRefId() + "." + extension.toLowerCase();
							item.setFileName(imageName);
							item.save();

							// Save the file
							saveFile(fileItem, imagePath, imageName);

						}
					}

					// create event log
					SysLog.log(data, SysLog.ITEM_ADDED, "Adding new item");
					data.setMessage("Your file uploaded successfully");
					return;

				}
			} else {
				context.put("error", true);
				data.setMessage("Item category not found");
				return;

			}
		} catch (Exception e) {

			log.error("An error occured adding new item: " + e.toString());
			data.setMessage("Error adding item");
			return;

		}
	}

	public void doUpdate(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		int refId = data.getParameters().getInt(REF_ID, -1);
		Item entry = Item.getItemByPrimaryKey(refId);

		int catId = data.getParameters().getInt("catId", -1);
		ItemCategory category = ItemCategory.getItemCategoryByPrimaryKey(catId);
		if (category != null) {

			if (entry != null) {

				String title = data.getParameters().getString("title", "");
				String desc = data.getParameters().getString("description", "");
				double price = data.getParameters().getDouble("price", 0.00);
				int quantity = data.getParameters().getInt("quantity", 0);

				if (!StringUtils.isEmpty(title)) {

					entry.setTitle(title);
					entry.setDescription(desc);
					entry.setQuantity(quantity);
					entry.setPrice(price);

					// TODO: add this to update
					entry.setShowSold(true);

					// upload the study file
					Part fileItem = data.getParameters().getPart("imageFile");
					if (fileItem != null) {
						String fileName = fileItem.getSubmittedFileName();
						String[] f = fileName.split("\\.");
						if (f.length > 1) {
							String extension = f[f.length - 1];

							// create the output directory
							String imagePath = Turbine.getRealPath("/") + UPLOAD_PATH;

							// create the directory if it does not exist
							createDirectory(imagePath);

							// remove the old file if one exists
							if (!StringUtils.isEmpty(entry.getFileName()))
								deleteFile(entry.getFileName());

							// assign file name and save
							String imageName = entry.getRefId() + "." + extension.toLowerCase();
							entry.setFileName(imageName);
							saveFile(fileItem, imagePath, imageName);
						}
					}

					entry.save();
					data.setMessage("Updated item");
					return;
				} else {
					context.put("error", true);
					data.setMessage("Title of item cannot be empty");
					return;
				}
			}
		} else {
			context.put("error", true);
			data.setMessage("Item category not found");
			return;
		}
	}

	/**
	 * Delete an item
	 * 
	 * @param pipelineData
	 * @param context
	 * @throws Exception
	 */
	public void doDelete(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;

		int refId = data.getParameters().getInt(REF_ID, -1);
		Item entry = Item.getItemByPrimaryKey(refId);
		if (entry != null) {

			if (entry.hasOrders() == false) {
				// remove the image file if there is one
				if (!StringUtils.isEmpty(entry.getFileName()))
					deleteFile(entry.getFileName());

				// now remove the item
				ItemPeer.doDelete(entry);
				data.setMessage("Item removed");
			} else {
				context.put("error", true);
				data.setMessage("Cannot delete an item that is linked to orders.");
				return;
			}

		} else {
			context.put("error", true);
			data.setMessage("Could not find item");
		}
	}

	private void saveFile(Part fileItem, String imagePath, String imageName) throws IOException, FileNotFoundException {
		InputStream is = fileItem.getInputStream();
		File targetFile = new File(imagePath + imageName);
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(IOUtils.toByteArray(is));

		// close streams
		outStream.close();
		is.close();
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

	/**
	 * Test if a file exists, if it does, delete it
	 * 
	 * @param filePath
	 */
	private static void deleteFile(String filePath) {
		try {
			File f = new File(filePath);
			if (f.exists() && !f.isDirectory()) {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

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

import java.io.File;
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

import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.HomePage;
import com.jivecast.webstore.util.SysLog;

/**
 * @author painter
 */
public class ConfigSQL extends SecureAction {

	/** Logging */
	private static Log log = LogFactory.getLog(ConfigSQL.class);

	public void doHomepage(PipelineData pipelineData, Context context) throws Exception {

		RunData data = (RunData) pipelineData;
		HomePage config = HomePage.getDefault();
		if (config != null) {

			// we will test these first
			String welcomeMsg = data.getParameters().getString("welcomeMessage").trim();

			// update the local config with parameters
			if (!StringUtils.isEmpty(welcomeMsg)) {


				// update the welcome message
				config.setWelcomeMessage(welcomeMsg);

				// upload new image file
				Part fileItem = data.getParameters().getPart("filename");
				if (fileItem != null) {
					String fileName = fileItem.getSubmittedFileName();
					String[] f = fileName.split("\\.");
					if (f.length > 1) {
						String extension = f[f.length - 1];

						// create the output directory
						String imagePath = Turbine.getRealPath("/") + "/images/uploads/";

						// create the directory if it does not exist
						createDirectory(imagePath);

						// assign file name and save
						String imageName = "homepage_image." + extension.toLowerCase();
						config.setFileName(imageName);

						InputStream is = fileItem.getInputStream();
						File targetFile = new File(imagePath + imageName);
						OutputStream outStream = new FileOutputStream(targetFile);
						outStream.write(IOUtils.toByteArray(is));

						// close streams
						outStream.close();
						is.close();

					}
				}

				config.save();
				data.setMessage("Updated webstore home page");
				return;
			} else {
				context.put("error", true);
				data.setMessage("Welcome message cannot be empty");
				return;
			}
		}

	}



	/**
	 * Import data from a CSV file
	 *
	 * @param data    Turbine information.
	 * @param context Context for web pages.
	 */
	public void doUpload(PipelineData pipelineData, Context context) {
		RunData data = (RunData) pipelineData;
		try {
			
			CompanyConfig company = CompanyConfig.getDefault();
			
			//
			// unpack the file and save to database
			//
			Part fileItem = data.getParameters().getPart("file");
			InputStream is = fileItem.getInputStream();
			company.setLogoFile(IOUtils.toByteArray(is));
			company.setModified(true);
			company.save();

			// create event log
			SysLog.log(data, SysLog.ADD_COMPANY_LOGO, "Adding new logo image");
			data.setMessage("Your file uploaded successfully");
			return;

		} catch (Exception e) {
			log.error("An error occured uploading new help file: " + e.toString());
			data.setMessage("File upload failed");
			return;
		}
	}
	
	
	public void doContact(PipelineData pipelineData, Context context) {

		RunData data = (RunData) pipelineData;
		try {
			CompanyConfig company = CompanyConfig.getDefault();

			// easiest way to get all values updated
			CompanyConfig entry = new CompanyConfig();
			data.getParameters().setProperties(entry);

			// copy over properties
			company.setCompanyName(entry.getCompanyName());
			company.setAdminEmail(entry.getAdminEmail());
			company.setPhone(entry.getPhone());
			company.setFax(entry.getFax());
			company.setTaxRate(entry.getTaxRate());			
			
			// address
			company.setAddressLine1(entry.getAddressLine1());
			company.setAddressLine2(entry.getAddressLine2());
			company.setCity(entry.getCity());
			company.setState(entry.getState());
			company.setZip(entry.getZip());
			company.setCountry("US");
			
			company.setModified(true);
			company.save();

			// create event log
			SysLog.log(data, SysLog.UPDATE_COMPANY_DETAIL, "Updating company contact info");

			data.setMessage("Your contact information has been saved");
			return;

		} catch (Exception e) {
			log.error("An error occured updating company info: " + e.toString());
			data.setMessage("Failed to update your company's contact info");
			return;
		}
	}	
	
	public void doPaypal(PipelineData pipelineData, Context context) {
		RunData data = (RunData) pipelineData;
		try {
			
			CompanyConfig company = CompanyConfig.getDefault();


			// easiest way to get all values updated
			CompanyConfig entry = new CompanyConfig();
			data.getParameters().setProperties(entry);
			
			company.setPaypalClientId(entry.getPaypalClientId());
			company.setPaypalClientSecret(entry.getPaypalClientSecret());
			company.setPaypalSandbox(entry.isPaypalSandbox());

			company.setModified(true);
			company.save();

			// create event log
			SysLog.log(data, SysLog.UPDATE_COMPANY_PAYPAL, "Updating company paypal credentials");

			data.setMessage("Paypal settings updated");
			return;

		} catch (Exception e) {
			log.error("An error occured updating paypal account: " + e.toString());
			data.setMessage("Failed to update paypal settings");
			return;
		}
	}
	
	
	public void doTerms(PipelineData pipelineData, Context context) {
		RunData data = (RunData) pipelineData;
		try {
			
			CompanyConfig company = CompanyConfig.getDefault();

			// update number of days a quote is valid
			int quoteDays = data.getParameters().getInt("quoteDays", 30);
			company.setQuoteDays(quoteDays);

			company.setQuoteTerms(data.getParameters().getString("quote").trim());
			company.setPOTerms(data.getParameters().getString("po").trim());
			company.setInvoiceTerms(data.getParameters().getString("invoice").trim());
			company.setConfirmationTerms(data.getParameters().getString("confirm").trim());

			company.setModified(true);
			company.save();

			// create event log
			SysLog.log(data, SysLog.UPDATE_COMPANY_TERMS, "Updating company terms and conditions");

			data.setMessage("Your terms and conditions have been updated");
			return;

		} catch (Exception e) {
			log.error("An error occured updating terms and conditions: " + e.toString());
			data.setMessage("Failed to update terms and conditions");
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

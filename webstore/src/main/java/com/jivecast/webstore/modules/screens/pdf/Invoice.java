package com.jivecast.webstore.modules.screens.pdf;

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

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.util.RunData;

import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.WebOrder;

/**
 * Customer Invoice PDF
 *
 * @author painter
 */
public class Invoice extends PdfScreen {

	/** Logging */
	private static Log log = LogFactory.getLog(Invoice.class);

	public ByteArrayOutputStream buildPdf(RunData data) throws Exception {
		try {
			CompanyConfig company = CompanyConfig.getDefault();
			if (company != null) {
				int refId = data.getParameters().getInt("refId", -1);
				WebOrder order = WebOrder.getWebOrderByPrimaryKey(refId);
				if (order != null) {
					// Set the file name to be returned
					String filename = order.getRefId() + "_invoice.pdf";
					javax.servlet.http.HttpServletResponse response = data.getResponse();
					response.setHeader("Content-Disposition", "inline;filename=\"" + filename + "\"");

					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					com.jivecast.webstore.pdf.Invoice.createInvoice(company, order, byteArrayOutputStream);
					return byteArrayOutputStream;
				} else {
					String printAll = data.getParameters().getString("all", "");
					if ( printAll.contentEquals("1") )
					{
						// print all unshipped orders in a single pdf
						// Set the file name to be returned
						String filename = "unshipped-orders.pdf";
						javax.servlet.http.HttpServletResponse response = data.getResponse();
						response.setHeader("Content-Disposition", "inline;filename=\"" + filename + "\"");

						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						com.jivecast.webstore.pdf.Invoice.printUnshipped(company, byteArrayOutputStream);
						return byteArrayOutputStream;
						
					}
				}
			}

			return null;

		} catch (Exception e) {
			log.error("Error generating customer invoice: " + e.toString());
			return null;
		}

	}

}

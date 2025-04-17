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

package com.jivecast.webstore.pdf;

import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.WebOrder;

/**
 * Description of the Class
 *
 * @author painter
 */
public class ShippingLabels {

	/** Logging */
	private static Log log = LogFactory.getLog(ShippingLabels.class);

	// set the fonts
	static Font whiteFont12 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.WHITE);

	/**
	 * Description of the Method
	 *
	 * @param order Description of the Parameter
	 * @param out   Description of the Parameter
	 */
	public static void createLabels(CompanyConfig company, OutputStream out) {

		Document document = new Document(PageSize.A4, 10, 10, 110, 65);
		try {

			PdfWriter writer = PdfWriter.getInstance(document, out);
			List<WebOrder> unshipped = WebOrder.getUnshippedOrders();

			// create localised event handler
			ipm_PageEvents events = new ipm_PageEvents(company);

			// no page counter on purchase orders
			events.setPageCount(false);
			events.setLogoImage(false);
			events.setPageType("Labels");
			writer.setPageEvent(events);

			document.open();

			// ---------------------------------------------------------------------------------------------
			int counter = 0;
			int max_per_page = 7;
			for (WebOrder order : unshipped) {

				try {
					// ------------------------------------------------------------------------------------------------------
					// setup invoice header
					PdfPTable shipLabel = shippingLabel(company, order);
					document.add(shipLabel);
					Formatter.addSpace(document);
					counter++;
					
					if (counter == max_per_page) {
						document.newPage();
						counter = 0;
					}
					
					
				} catch (Exception f) {
					log.error("PDF Creation error: " + f.toString());
					System.err.println("Error: " + f.toString());
				}

			}

			// ------------------------------------------------------------------------------------------------------
			document.close();

		} catch (Exception e) {
			log.error("PDF Creation error: " + e.toString());
		}

	}


	
	/**
	 * Description of the Method
	 *
	 * @param order Description of the Parameter
	 * @return Description of the Return Value
	 */
	private static PdfPTable shippingLabel(CompanyConfig company, WebOrder order) {
		PdfPTable qHeader = new PdfPTable(2);
		qHeader.getDefaultCell().setPadding(2);

		// Ship from:
		PdfPCell cell = Formatter.shipFrom(company);
		qHeader.addCell(cell);

		// Ship to:
		cell = Formatter.dataCell("Ship To:", order.getShippingInfo());
		qHeader.addCell(cell);
		return qHeader;
	}

}

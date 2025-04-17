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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.jivecast.webstore.om.CompanyConfig;

/**
 * ipm_PageEventHandler responsible for adding proper headers, footers for all
 * ipm documents
 *
 * @author painter
 */
public class ipm_PageEvents extends PdfPageEventHelper {

	/** Logging */
	private static Log log = LogFactory.getLog(ipm_PageEvents.class);

	private int orderid = 0;
	private String pageType = "";

	// boolean flags
	private boolean showPageNumbers = true;
	private boolean showLogo = true;

	// need user info to print company profile headers and footers
	private CompanyConfig company;

	// This is the contentbyte object of the writer
	private PdfContentByte cb;

	// we will put the final number of pages in a template
	private PdfTemplate tmpl;

	private ipm_HeaderFooter hd = new ipm_HeaderFooter();

	// this is the BaseFont we are going to use for the header / footer
	private BaseFont bf = null;

	/**
	 * default constructor
	 */
	public ipm_PageEvents(CompanyConfig company) {
		// Log.info("Initialising new page event handler");
		this.company = company;
		this.showPageNumbers = true;
		this.showLogo = true;
		this.orderid = 0;
		this.pageType = "";
		return;
	}

	/**
	 * Sets the orderID attribute of the ipm_PageEvents object
	 *
	 * @param id
	 *            The new orderID value
	 */
	public void setOrderID(int id) {
		this.orderid = id;
	}

	/**
	 * Sets the pageType attribute of the ipm_PageEvents object
	 *
	 * @param val
	 *            The new pageType value
	 */
	public void setPageType(String val) {
		this.pageType = val;
	}

	/**
	 * Sets the pageCount attribute of the ipm_PageEvents object
	 *
	 * @param flag
	 *            The new pageCount value
	 */
	public void setPageCount(boolean flag) {
		this.showPageNumbers = flag;
	}

	/**
	 * Sets the logoImage attribute of the ipm_PageEvents object
	 *
	 * @param flag
	 *            The new logoImage value
	 */
	public void setLogoImage(boolean flag) {
		this.showLogo = flag;
	}

	/*
	 * Getters
	 */
	/**
	 * Gets the pageCount attribute of the ipm_PageEvents object
	 *
	 * @return The pageCount value
	 */
	public boolean getPageCount() {
		return this.showPageNumbers;
	}

	/**
	 * Gets the logoImage attribute of the ipm_PageEvents object
	 *
	 * @return The logoImage value
	 */
	public boolean getLogoImage() {
		return this.showLogo;
	}

	// we override the onOpenDocument method
	/**
	 * Description of the Method
	 *
	 * @param writer
	 *            Description of the Parameter
	 * @param document
	 *            Description of the Parameter
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
		try {
			bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			cb = writer.getDirectContent();
			tmpl = cb.createTemplate(50, 50);

		} catch (DocumentException de) {

			log.error("ipm_pageEvent Document exception error: " + de.toString());

		} catch (IOException ioe) {

			log.error("ipm_pageEvent IO exception error: " + ioe.toString());
		}
	}

	// we override the onEndPage method
	/**
	 * Description of the Method
	 *
	 * @param writer
	 *            Description of the Parameter
	 * @param document
	 *            Description of the Parameter
	 */
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			if (this.getLogoImage()) {
				// header
				PdfPTable headTable = hd.getHeader(this.company);
				headTable.writeSelectedRows(0, -1, 4, 840, cb);
			}

		} catch (Exception e) {
			log.error("ipm_PageEvent onPageEnd() error: " + e.toString());
			return;
		}

		try {
			// footer
			PdfPTable footTable = hd.getFooter(this.company, pageType, orderid);
			footTable.writeSelectedRows(0, -1, 4, 64, cb);

			if (this.getPageCount()) {
				// footer page count section
				int pageN = writer.getPageNumber();
				String text = "Page " + pageN + " of ";
				float len = bf.getWidthPoint(text, 8);
				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setTextMatrix(550, 5);
				cb.showText(text);
				cb.endText();
				cb.addTemplate(tmpl, 550 + len, 5);
			}

		} catch (Exception e) {
			log.error("ipm_PageEvent footer error: " + e.toString());
			return;
		}
	}

	// we override the onCloseDocument method
	/**
	 * Description of the Method
	 *
	 * @param writer
	 *            Description of the Parameter
	 * @param document
	 *            Description of the Parameter
	 */
	public void onCloseDocument(PdfWriter writer, Document document) {
		tmpl.beginText();
		tmpl.setFontAndSize(bf, 8);
		tmpl.showText(String.valueOf(writer.getPageNumber()));
		tmpl.endText();
	}

}

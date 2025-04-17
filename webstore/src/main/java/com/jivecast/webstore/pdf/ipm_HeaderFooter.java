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

// Headers and Footer defaults for IPM formatted PDF files
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.jivecast.webstore.om.CompanyConfig;

/**
 * ipm_HeaderFooter is used to build our default header and footer info
 *
 * @author painter
 */
public class ipm_HeaderFooter {

	/** Logging */
	private static Log log = LogFactory.getLog(ipm_HeaderFooter.class);

	/**
	 * getFooter() is our standard document footer
	 *
	 * @param l
	 *            Description of the Parameter
	 * @param lNum
	 *            Description of the Parameter
	 * @return The footer value
	 */
	public PdfPTable getFooter(CompanyConfig company, String l, int lNum) {

		// setup the page location
		String loc = l + " " + lNum;

		// set the font for company header
		Font font8 = FontFactory.getFont(FontFactory.TIMES, 8, BaseColor.BLACK);
		Font font8b = FontFactory.getFont(FontFactory.TIMES_BOLD, 8, BaseColor.BLACK);

		// Font font8 = new Font(Font.TIMES_ROMAN, 8);
		// Font font8b = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);

		Phrase location = new Phrase(10, loc, font8b);
		String trm = "";

		if (l.contentEquals("Quotation")) {
			trm = "Terms & Conditions\n" + company.getQuoteTerms() + "\n" + signatureLine();
		} else if (l.contentEquals("Invoice")) {
			trm = "Terms & Conditions\n" + company.getInvoiceTerms();
		} else if (l.contentEquals("Consolidated Invoice")) {
			trm = "Terms & Conditions\n" + company.getInvoiceTerms();
		} else if (l.contentEquals("Purchase Order")) {
			trm = "Terms & Conditions\n" + company.getPOTerms();
		} else if (l.contentEquals("Order")) {
			trm = "Terms & Conditions\n" + company.getConfirmationTerms() + "\n" + signatureLine();
		} else if (l.contentEquals("Statement") || l.contentEquals("Office Copy") || l.contentEquals("Order Packing List") || l.contentEquals("Labels") ) {
			// no terms printed on a statement
			trm = "";
		} else {
			trm = "Terms & Conditions\n" + company.getInvoiceTerms();
		}		

		Chunk cterms = new Chunk(trm, font8);
		Phrase pterms = new Phrase(10, cterms);

		// build a table split in two columns
		PdfPTable tFoot = new PdfPTable(2);
		tFoot.setTotalWidth(580);
		// entire width of page
		tFoot.setWidthPercentage(100);
		// 100% for header

		PdfPCell terms = new PdfPCell(pterms);
		terms.setColspan(2);
		terms.setHorizontalAlignment(Element.ALIGN_LEFT);
		terms.setVerticalAlignment(Element.ALIGN_TOP);
		terms.setPadding(2);

		// put terms in a box
		if (!trm.equals("")) {
			terms.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
		}

		tFoot.addCell(terms);

		PdfPCell locationCell = new PdfPCell(location);
		locationCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		locationCell.setVerticalAlignment(Element.ALIGN_TOP);
		locationCell.setBorder(Rectangle.NO_BORDER);
		tFoot.addCell(locationCell);

		Phrase empty = new Phrase("");
		PdfPCell rightCell = new PdfPCell(empty);
		rightCell.setBorder(Rectangle.NO_BORDER);
		tFoot.addCell(rightCell);

		return tFoot;
	}

	/**
	 * Gets the header attribute of the ipm_HeaderFooter object
	 *
	 * @return The header value
	 */
	public PdfPTable getHeader(CompanyConfig company) {
		try {
			// set the font for company header
			Font fatFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK);

			// Company logo set as image
			Image logo = Image.getInstance(getLogoImg(company));

			// build a table split in two columns
			PdfPTable tHead = new PdfPTable(2);
			tHead.setTotalWidth(800);
			// entire width of page
			tHead.setWidthPercentage(100);
			// 100% for header

			PdfPCell leftCell = new PdfPCell(logo);
			leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			leftCell.setVerticalAlignment(Element.ALIGN_TOP);
			leftCell.setBorder(Rectangle.NO_BORDER);
			tHead.addCell(leftCell);

			Phrase compCell = new Phrase(18, getCompanyInfo(company), fatFont);
			PdfPCell rightCell = new PdfPCell(compCell);
			rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			rightCell.setVerticalAlignment(Element.ALIGN_TOP);
			rightCell.setBorder(Rectangle.NO_BORDER);
			tHead.addCell(rightCell);
			return tHead;
		} catch (Exception e) {
			log.error("ipm header footer - getHeader() error: " + e.toString());
			return null;
		}
	}

	/**
	 * Gets the logoImg attribute of the ipm_HeaderFooter class
	 *
	 * @return The logoImg value
	 */
	public static Image getLogoImg(CompanyConfig company) {
		try {

			if (company != null) {
				// load image from database
				byte[] bytes = company.getLogoFile();
				// create image from bytes
				Image img = Image.getInstance(java.awt.Toolkit.getDefaultToolkit().createImage(bytes), null);

				// images are automatically reduced to 72 dpi regardless of
				// their original specs, so
				// logo needs to be 1583x417 reduced to 24% becomes 380x100
				// without loss
				img.scalePercent(24);
				return img;

			} else {

				// read file into byte array so Image can load
				String filePath = Turbine.getRealPath("images/business_logo.jpg");
				File file = new File(filePath);
				if (file.exists()) {
					// read file
					InputStream is = new FileInputStream(file);
					long length = file.length();
					byte[] bytes = new byte[(int) length];
					int offset = 0;
					int numRead = 0;
					while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
						offset += numRead;
					}

					// Ensure all the bytes have been read in
					if (offset < bytes.length) {
						log.error("Could not completely read file " + file.getName());
						is.close();
						return null;
					}

					// Close the input stream and return bytes
					is.close();

					// create image from bytes
					Image img = Image.getInstance(java.awt.Toolkit.getDefaultToolkit().createImage(bytes), null);

					// images are automatically reduced to 72 dpi regardless of
					// their original specs, so
					// logo needs to be 1583x417 reduced to 24% becomes 380x100
					// without loss
					img.scalePercent(24);
					return img;
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			log.error("header could not fetch logo: " + e.toString());
			return null;
		}
	}

	/**
	 * company info is static for now
	 *
	 * @return The companyInfo value
	 */
	public static String getCompanyInfo(CompanyConfig company) {
		try {
			StringBuilder info = new StringBuilder();
			info.append(company.getCompanyName());
			info.append(System.getProperty("line.separator"));
			info.append(company.getShippingInfo());
			info.append(System.getProperty("line.separator"));
			return info.toString();
		} catch (Exception e) {
			return "Company info is missing\n";
		}
	}

	/**
	 * build a signature line
	 *
	 * @return Description of the Return Value
	 */
	public static String signatureLine() {
		String text = "       " + " Name: ___________________________________________________ "
				+ " Signature: ______________________________________________ " + " Date: __________________\n";
		return text;
	}

}

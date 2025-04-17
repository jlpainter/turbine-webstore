
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
// Formatting utilities for pdf creation

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.jivecast.webstore.om.CompanyConfig;

/**
 * Description of the Class
 *
 * @author painter
 */
public class Formatter {

	// set the font for company header
	static Font font8 = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
	static Font font8b = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.BLACK);
	static Font font10 = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
	static Font whiteFont12 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.WHITE);
	static Font font12 = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
	static Font font12b = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
	static Font font16 = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
	static Font font16b = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);

	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static Font smallFont() {
		return font8;
	}

	/**
	 * Description of the Method
	 *
	 * @param bold Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static Font smallFont(String bold) {
		if (bold.equals("bold")) {
			return font8b;
		} else {
			return font8;
		}
	}

	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static Font medFont() {
		return font12;
	}

	/**
	 * Description of the Method
	 *
	 * @param bold Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static Font medFont(String bold) {
		if (bold.equals("bold")) {
			return font12b;
		} else {
			return font12;
		}
	}

	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static Font largeFont() {
		return font16;
	}

	/**
	 * Description of the Method
	 *
	 * @param bold Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static Font largeFont(String bold) {
		if (bold.equals("bold")) {
			return font16b;
		} else {
			return font16;
		}
	}

	// --------------------------------------------------------------------

	/**
	 * Adds a feature to the Space attribute of the Formatter class
	 *
	 * @param doc The feature to be added to the Space attribute
	 */
	public static void addSpace(Document doc) {
		try {
			Paragraph x = new Paragraph("\n");
			doc.add(x);
		} catch (Exception e) {
		}
		return;
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param font  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell whiteLabel(String label, Font font) {
		String text = "";
		if (label != null) {
			text = label;
		}
		PdfPCell cell = clearCell(text, font);
		cell.setGrayFill(0.2f);
		return cell;
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param font  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell borderCell(int label, Font font) {
		return borderCell(Integer.toString(label), font);
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param font  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell borderCell(String label, Font font) {
		if (!StringUtils.isEmpty(label)) {
			PdfPCell cell = clearCell(label, font);
			cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
			return cell;
		} else {
			PdfPCell cell = clearCell("", font);
			cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
			return cell;
		}
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param font  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell clearCell(int label, Font font) {
		return clearCell(Integer.toString(label), font);
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param font  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell clearCell(String label, Font font) {
		String text = "";
		if (!StringUtils.isEmpty(label)) {
			text = label;
		}

		Chunk ck = new Chunk(text, font);
		Phrase p = new Phrase(ck);
		PdfPCell cell = new PdfPCell(p);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return cell;
	}

	/**
	 * Description of the Method
	 *
	 * @param table Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell tableCell(PdfPTable table) {
		PdfPCell cell = new PdfPCell(table);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		return cell;
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param data  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell dataCell(String label, String data) {
		String text = "";

		if (label != null) {
			text = label;
		}
		text = text + "\n";
		Chunk l = new Chunk(text, font8);

		if (data != null) {
			text = data;
		} else {
			text = "";
		}
		Chunk d = new Chunk(text, font12b);
		Phrase p = new Phrase(l);
		p.add(d);

		PdfPCell cell = new PdfPCell(p);
		cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		return cell;
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param data  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell shipFrom(CompanyConfig config) {
		String text = "Ship From: \n";
		Chunk l = new Chunk(text, font12b);

		text = config.getCompanyName() + "\n";
		text = text + config.getShippingInfo();

		Chunk d = new Chunk(text, font8);
		Phrase p = new Phrase(l);
		p.add(d);

		PdfPCell cell = new PdfPCell(p);
		cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		return cell;
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param data  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell grayDataCell(String label, String data) {
		label = label + "\n";
		Chunk l = new Chunk(label, font8);
		Chunk d = new Chunk(data, font12b);
		Phrase p = new Phrase(l);
		p.add(d);

		PdfPCell cell = new PdfPCell(p);
		cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setGrayFill(0.9f);
		return cell;
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param font  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell grayCell(String label, Font font) {
		PdfPCell cell = clearCell(label, font);
		cell.setGrayFill(0.9f);
		return cell;
	}

	/**
	 * Description of the Method
	 *
	 * @param label Description of the Parameter
	 * @param font  Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPCell grayCell(int label, Font font) {
		PdfPCell cell = clearCell(Integer.toString(label), font);
		cell.setGrayFill(0.9f);
		return cell;
	}

}

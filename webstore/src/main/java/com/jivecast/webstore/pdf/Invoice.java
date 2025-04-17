
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
// Create a invoice pdf file

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.criteria.Criteria;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.Item;
import com.jivecast.webstore.om.OrderDetail;
import com.jivecast.webstore.om.WebOrder;
import com.jivecast.webstore.om.WebOrderPeer;
// use localdate object for date formatting
import com.jivecast.webstore.util.LocalDate;
import com.jivecast.webstore.util.MoneyFormat;

/**
 * Description of the Class
 *
 * @author painter
 */
public class Invoice {

	/** Logging */
	private static Log log = LogFactory.getLog(Invoice.class);

	// set the fonts
	static Font whiteFont12 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.WHITE);
	static MoneyFormat mf = new MoneyFormat();

	private static double OrderSubtotal = 0.0;
	private static double TaxTotal = 0.0;
	private static double TaxPercent = 0.0;
	private static double OutstandingTotal = 0.0;

	/**
	 * Description of the Method
	 *
	 * @param order Description of the Parameter
	 * @param out   Description of the Parameter
	 */
	public static void createInvoice(CompanyConfig company, WebOrder order, OutputStream out) {

		TaxPercent = company.getTaxRate();

		Document document = new Document(PageSize.A4, 10, 10, 110, 65);
		try {

			PdfWriter writer = PdfWriter.getInstance(document, out);

			// create localised event handler
			ipm_PageEvents events = new ipm_PageEvents(company);
			events.setOrderID(order.getRefId());
			events.setPageType("Invoice");

			writer.setPageEvent(events);
			document.open();

			// ------------------------------------------------------------------------------------------------------
			// setup invoice header
			PdfPTable qHeader = invoiceHeader(order);
			document.add(qHeader);
			// Formatter.addSpace(document);
			// ------------------------------------------------------------------------------------------------------

			// add line items and calculate tax on items
			try {
				List<OrderDetail> items = order.getDetails();
				PdfPTable invoiceTable = lineItems(items);
				document.add(invoiceTable);
				Formatter.addSpace(document);
			} catch (Exception e) {
			}
			// ------------------------------------------------------------------------------------------------------
			// complete totals
			PdfPTable totalTable = getTotal(order);
			document.add(totalTable);
			Formatter.addSpace(document);
			// ------------------------------------------------------------------------------------------------------
			document.close();

		} catch (Exception e) {
			log.error("PDF Creation error: " + e.toString());
		}

	}

	/**
	 * Gets the total attribute of the Invoice class
	 *
	 * @param order Description of the Parameter
	 * @return The total value
	 */
	public static PdfPTable getTotal(WebOrder order) {
		PdfPTable notesTable = new PdfPTable(1);
		PdfPTable totalTable = new PdfPTable(2);

		try {

			double freight = order.getShipping();
			double total = 0.0;

			total = OrderSubtotal + freight + TaxTotal;

			String sSub = mf.custDollarFormat(OrderSubtotal);
			String sTax = mf.taxFormat(TaxPercent);
			String sFreight = mf.custDollarFormat(freight);
			String sTaxCharge = mf.custDollarFormat(TaxTotal);
			String sTotal = mf.custDollarFormat(total);

			PdfPCell notesCell = Formatter.borderCell(("Notes\n"), Formatter.medFont());
			notesCell.setVerticalAlignment(Element.ALIGN_TOP);
			notesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			notesTable.addCell(notesCell);

			// get money side

			// subtotal
			PdfPCell sl = Formatter.grayCell("SUBTOTAL", Formatter.medFont());
			sl.setHorizontalAlignment(Element.ALIGN_RIGHT);
			sl.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			totalTable.addCell(sl);
			PdfPCell st = Formatter.borderCell(sSub, Formatter.medFont());
			st.setHorizontalAlignment(Element.ALIGN_RIGHT);
			totalTable.addCell(st);

			// freight
			PdfPCell fl = Formatter.grayCell("SHIPPING", Formatter.medFont());
			fl.setHorizontalAlignment(Element.ALIGN_RIGHT);
			fl.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			totalTable.addCell(fl);

			PdfPCell ft = Formatter.borderCell(sFreight, Formatter.medFont());
			ft.setHorizontalAlignment(Element.ALIGN_RIGHT);
			totalTable.addCell(ft);

			// tax
			PdfPCell tl = Formatter.grayCell("SALES TAX (" + sTax + ")%", Formatter.medFont());
			tl.setHorizontalAlignment(Element.ALIGN_RIGHT);
			tl.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			totalTable.addCell(tl);

			PdfPCell tt = Formatter.borderCell(sTaxCharge, Formatter.medFont());
			tt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			totalTable.addCell(tt);

		} catch (Exception e) {
			log.error("Invoice error: " + e.toString());
			return null;
		}

		// put it all together
		PdfPTable layout = new PdfPTable(2);
		layout.setWidthPercentage(100);

		PdfPCell notesCellLayout = Formatter.tableCell(notesTable);
		PdfPCell totalCellLayout = Formatter.tableCell(totalTable);

		layout.addCell(notesCellLayout);
		layout.addCell(totalCellLayout);

		return layout;
	}

	/**
	 * Description of the Method
	 *
	 * @param order Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPTable invoiceHeader(WebOrder order) {


		PdfPTable qHeader = new PdfPTable(6);
		qHeader.getDefaultCell().setPadding(2);

		// ROW 1 -- INVOICE | # | DATE | DUE DATE | DESCRIPTION
		PdfPCell cell = Formatter.clearCell("INVOICE", Formatter.largeFont("bold"));
		qHeader.addCell(cell);

		cell = Formatter.dataCell("Invoice Number", String.valueOf(order.getRefId()));
		qHeader.addCell(cell);

		// invoice date
		try {
			if (order.getOrderDate() == null) {
				order.setOrderDate(new Date());
				order.setNew(false);
				order.save();
			}
		} catch (Exception e) {
			log.error("Could not update invoice date where date was missing: " + e.toString());
		}

		cell = Formatter.dataCell("Order Date", LocalDate.reportDate(order.getOrderDate()));
		qHeader.addCell(cell);

		// setup address info
		String address = order.getShippingInfo();


		// ROW 2 -- CUSTOMER | SHIP TO

		// get customer side info
		cell = Formatter.borderCell("Customer", Formatter.smallFont());
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
		qHeader.addCell(cell);

		cell = Formatter.borderCell(address, Formatter.medFont());
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setColspan(2);
		qHeader.addCell(cell);

		// setup ship to side
		cell = Formatter.borderCell("Ship To", Formatter.smallFont());
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
		qHeader.addCell(cell);

		cell = Formatter.borderCell(address, Formatter.medFont());
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setColspan(2);
		qHeader.addCell(cell);


		cell = Formatter.dataCell("Ship Via", "USPS");
		qHeader.addCell(cell);

		// ROW 4 -- PO | DT ORDERED 
		cell = Formatter.dataCell("Order Number", new Integer(order.getRefId()).toString());
		qHeader.addCell(cell);

		cell = Formatter.dataCell("Date Ordered", LocalDate.reportDate(order.getOrderDate()));
		qHeader.addCell(cell);
		qHeader.setWidthPercentage(100);
		return qHeader;
	}

	/**
	 * Description of the Method
	 *
	 * @param items Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static PdfPTable lineItems(List<OrderDetail> items) {

		try {
			// items invoiced
			PdfPTable invoiceTable = new PdfPTable(5);

			invoiceTable.getDefaultCell().setPadding(3);
			int headerwidths[] = { 15, 15, 40, 15, 15 };
			invoiceTable.setWidths(headerwidths);
			invoiceTable.setWidthPercentage(100);
			// percentage

			PdfPCell cell = Formatter.whiteLabel("Quantity", whiteFont12);
			invoiceTable.addCell(cell);
			cell = Formatter.whiteLabel("Item", whiteFont12);
			invoiceTable.addCell(cell);
			cell = Formatter.whiteLabel("Item Description", whiteFont12);
			invoiceTable.addCell(cell);
			cell = Formatter.whiteLabel("Unit Cost", whiteFont12);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			invoiceTable.addCell(cell);
			cell = Formatter.whiteLabel("Line Cost", whiteFont12);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			invoiceTable.addCell(cell);
			invoiceTable.setHeaderRows(1);
			// this is the end of the table header

			OrderSubtotal = 0.0;
			TaxTotal = 0.0;

			for ( OrderDetail detail : items ) {
				
				Item item = detail.getItem();
				if ( detail.getItemQuantity() > 0 ) {
				

					double totalCost = detail.getItemPrice() * detail.getItemQuantity();
					OrderSubtotal = OrderSubtotal + totalCost;

					TaxTotal = TaxTotal + (totalCost * TaxPercent);

					String sc = mf.dollarFormat(detail.getItemPrice());
					String se = mf.dollarFormat(totalCost);

					cell = Formatter.clearCell(detail.getItemQuantity(), Formatter.medFont());
					invoiceTable.addCell(cell);

					cell = Formatter.clearCell(item.getTitle(), Formatter.medFont());
					invoiceTable.addCell(cell);

					// description
					String description = "";
					if (item.getDescription().length() > 0) {
						description += item.getDescription();
					}

					cell = Formatter.clearCell(description, Formatter.medFont());
					invoiceTable.addCell(cell);
					// description

					cell = Formatter.clearCell(sc, Formatter.medFont());

					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					invoiceTable.addCell(cell);

					cell = Formatter.clearCell(se, Formatter.medFont());

					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					invoiceTable.addCell(cell);
				}
			}

			return invoiceTable;
		} catch (Exception e) {

			return null;
		}

	}

	public static void printUnshipped(CompanyConfig company, ByteArrayOutputStream byteArrayOutputStream) {

		Document document = new Document(PageSize.A4, 10, 10, 110, 65);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
			List<WebOrder> unshipped = WebOrder.getUnshippedOrders();
					
			// create localised event handler
			ipm_PageEvents events = new ipm_PageEvents(company);

			// no page counter on purchase orders
			events.setPageCount(false);
			events.setLogoImage(true);
			events.setOrderID(0);
			events.setPageType("Invoice");
			writer.setPageEvent(events);
			document.open();

			// ---------------------------------------------------------------------------------------------
			// we want a page break for each order

			// ---------------------------------------------------------------------------------------------
			// iterate over all suppliers for this order and create a page break
			// between each one
			for (WebOrder order : unshipped ) {


				// ------------------------------------------------------------------------------------------------------
				// setup invoice header
				PdfPTable qHeader = invoiceHeader(order);
				document.add(qHeader);
				// ------------------------------------------------------------------------------------------------------
				// add line items and calculate tax on items
				try {
					List<OrderDetail> items = order.getDetails();
					PdfPTable invoiceTable = lineItems(items);
					document.add(invoiceTable);
					Formatter.addSpace(document);
				} catch (Exception e) {
				}
				// ------------------------------------------------------------------------------------------------------
				// complete totals
				PdfPTable totalTable = getTotal(order);
				document.add(totalTable);
				Formatter.addSpace(document);				

				// add a page
				document.newPage();
			}

			// ---------------------------------------------------------------------------------------------
			document.close();

		} catch (Exception e) {

			log.error("PDF Creation error: " + e.toString());

		}

		
	}

}

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

package com.jivecast.webstore.om;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.criteria.Criteria;

/**
 * The skeleton for this class was autogenerated by Torque on:
 *
 * [Mon Apr 20 09:07:46 EDT 2020]
 *
 * You should add additional methods to this class to meet the application
 * requirements. This class will only be generated as long as it does not
 * already exist in the output directory.
 */

public class WebOrder extends com.jivecast.webstore.om.BaseWebOrder {
	/** Serial version */
	private static final long serialVersionUID = 1587388066027L;

	private static Log log = LogFactory.getLog(WebOrder.class);

	/**
	 * Get current order status
	 * @return
	 */
	public String getStatus() {
		String status = "Unpaid";
		if ( this.isPaid() == true )
		{
			status = "Paid";
			if ( this.isShipped() == true )
			{
				status = "Complete";
			}
		}
		
		return status;
	}
	
	public String getShippingInfo() {
		
		String shipping = new String();
		
		if (!StringUtils.isEmpty(this.getRecipientName()))
			shipping = this.getRecipientName() + "\n";
		
		if (!StringUtils.isEmpty(this.getAddressLine1()))
			shipping = shipping + this.getAddressLine1() + "\n";
		
		if (!StringUtils.isEmpty(this.getAddressLine2()))
			shipping = shipping + this.getAddressLine2() + "\n";
		
		shipping = shipping + this.getCity() + ", " + this.getState() + "\n";
		shipping = shipping + this.getZip() + "\n";
		shipping = shipping + this.getCountry() + "\n";
		return shipping;
	}

	public static WebOrder getWebOrderByPrimaryKey(int refId) {
		try {
			// Convert all product names to lower case
			Criteria criteria = new Criteria();
			criteria.where(WebOrderPeer.REF_ID, refId);
			return WebOrderPeer.doSelectSingleRecord(criteria);
		} catch (Exception e) {
			log.error("Order not found: " + refId);
			return null;
		}
	}

	public static WebOrder getOrderFromPaypalCustom(String custom) {
		try {
			// Convert all product names to lower case
			Criteria criteria = new Criteria();
			criteria.where(WebOrderPeer.PAYPAL_CUSTOM, custom);
			return WebOrderPeer.doSelectSingleRecord(criteria);
		} catch (Exception e) {
			log.error("Order not found: " + custom);
			return null;
		}
	}

	public List<OrderDetail> getDetails() {
		try {
			// Convert all product names to lower case
			Criteria criteria = new Criteria();
			criteria.where(OrderDetailPeer.ORDER_ID, this.getRefId());
			return (List<OrderDetail>) OrderDetailPeer.doSelect(criteria);
		} catch (Exception e) {
			log.error("Order details not found: " + this.getRefId());
			return null;
		}
	}

	/**
	 * Set the order complete and remove inventory
	 */
	public void setPaymentCompleted() {

		try {

			List<OrderDetail> details = this.getDetails();
			for (OrderDetail detail : details) {
				Item item = detail.getItem();
				int qty = detail.getItemQuantity();

				item.setQuantity(item.getQuantity() - qty);
				item.save();
			}

			this.setCancel(false);
			this.setShipped(false);
			this.setPaid(true);
			this.save();

		} catch (Exception e) {
			log.error("Error setting order completed");
		}
	}

	/**
	 * Revoke order and put inventory back
	 */
	public void revokeOrder() {

		try {

			List<OrderDetail> details = this.getDetails();
			for (OrderDetail detail : details) {
				Item item = detail.getItem();
				int qty = detail.getItemQuantity();

				item.setQuantity(item.getQuantity() + qty);
				item.save();
			}

			this.setCancel(true);
			this.setShipped(false);
			this.setPaid(false);
			this.save();

		} catch (Exception e) {
			log.error("Error setting order completed");
		}
	}

	public static List<WebOrder> getUnshippedOrders() {
		try {
			// Convert all product names to lower case
			Criteria criteria = new Criteria();
			criteria.where(WebOrderPeer.PAID, true);
			criteria.where(WebOrderPeer.CANCEL, false);
			criteria.where(WebOrderPeer.SHIPPED, false);
			return WebOrderPeer.doSelect(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	public static List<WebOrder> getCompletedOrders() {
		try {
			// Convert all product names to lower case
			Criteria criteria = new Criteria();
			criteria.where(WebOrderPeer.PAID, true);
			criteria.where(WebOrderPeer.CANCEL, false);
			criteria.where(WebOrderPeer.SHIPPED, true);
			return WebOrderPeer.doSelect(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	public static List<WebOrder> getIncompletedOrders() {
		try {
			// Convert all product names to lower case
			Criteria criteria = new Criteria();
			criteria.where(WebOrderPeer.PAID, false);
			criteria.where(WebOrderPeer.CANCEL, false);
			criteria.where(WebOrderPeer.SHIPPED, false);
			return WebOrderPeer.doSelect(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	
}

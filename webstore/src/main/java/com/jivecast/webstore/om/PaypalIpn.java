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

import java.util.List;
import java.util.Vector;

import org.apache.torque.criteria.Criteria;
import org.apache.torque.util.CountHelper;

/**
 * The skeleton for this class was autogenerated by Torque on:
 *
 * [Sat Apr 18 18:43:39 EDT 2020]
 *
 * You should add additional methods to this class to meet the application
 * requirements. This class will only be generated as long as it does not
 * already exist in the output directory.
 */

public class PaypalIpn extends com.jivecast.webstore.om.BasePaypalIpn {
	/** Serial version */
	private static final long serialVersionUID = 1587249819166L;

	public double getProfit() {
		if (this.getAmountPaid() > 0)
			return this.getAmountPaid() - this.getFee();
		else
			return 0.00;
	}

	/**
	 * Get the payment history using pagination method
	 */
	public static List<PaypalIpn> getAllPayments(int limit, int idx) {

		// int limit = 5;
		List<PaypalIpn> events = new Vector<PaypalIpn>();
		List<Integer> postId = new Vector<Integer>();

		try {
			Criteria criteria = new Criteria();
			// Newest to oldest
			criteria.addDescendingOrderByColumn(PaypalIpnPeer.PAYMENT_DATE);
			List<PaypalIpn> history = (List<PaypalIpn>) PaypalIpnPeer.doSelect(criteria);

			// Paginate the results
			int startId = 0;
			int endId = 0;
			int currId = 0;

			// Check index size
			if (history.size() > limit) {
				// Set the start and end ID
				startId = limit * idx;
				endId = startId + limit;
				for (PaypalIpn h : history) {
					if (startId <= currId && currId < endId) {
						events.add(h);
					}
					currId++;
				}

			} else {
				for (PaypalIpn h : history)
					events.add(h);
			}

			return events;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Determine number of pages to show the posts
	 * 
	 * @param product_key
	 * @return
	 */
	public static List<Integer> getEventPages(int limit) {
		try {
			List<Integer> pages = new Vector<Integer>();

			// setup basic criteria
			Criteria criteria = new Criteria();
			criteria.where(PaypalIpnPeer.REF_ID, 0, Criteria.GREATER_EQUAL);
			criteria.setDistinct();

			CountHelper counter = new CountHelper();
			int count = counter.count(criteria, PaypalIpnPeer.REF_ID);
			int page_count = (int) Math.ceil(count / limit) + 1;
			for (int i = 0; i < page_count; i++)
				pages.add(i);
			return pages;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get a single IPN report
	 * 
	 * @param refId
	 * @return
	 */
	public static PaypalIpn getPaypalIpn(int refId) {
		try {
			Criteria criteria = new Criteria();
			criteria.where(PaypalIpnPeer.REF_ID, refId);
			return PaypalIpnPeer.doSelectSingleRecord(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	public String getAddress() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getAddressStreet());
		sb.append("\n");
		sb.append(this.getAddressCity());
		sb.append(", ");
		sb.append(this.getAddressState());
		sb.append(" ");
		sb.append(this.getAddressZip());
		sb.append("\n");
		return sb.toString();
	}

}

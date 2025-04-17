package com.jivecast.webstore.util.tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.pull.ApplicationTool;

import com.jivecast.webstore.om.Item;

/**
 *  ------------------------------------------------------------------
 *  Jeffery L Painter, <jeff@jivecast.com>
 *
 *  Copyright (c) 2020-2025 JiveCast
 *  All Rights Reserved.
 *
 *  THE PROGRAM IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
 *  LIMITATION, WARRANTIES THAT THE PROGRAM IS FREE OF
 *  DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR
 *  NON-INFRINGING. THE ENTIRE RISK AS TO THE QUALITY AND
 *  PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD ANY PART
 *  OF THE PROGRAM PROVE DEFECTIVE IN ANY RESPECT, YOU
 *  (NOT JiveCast) ASSUME THE COST OF ANY NECESSARY SERVICING,
 *  REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES
 *  AN ESSENTIAL PART OF THIS LICENSE. NO USE OF
 *  THE PROGRAM IS AUTHORIZED HEREUNDER EXCEPT
 *  UNDER THIS DISCLAIMER.
 *
 *  ------------------------------------------------------------------
 */

/**
 * Monitor user's active subscription
 *
 * @author painter
 */
public class CartTool implements ApplicationTool {

	/** Logging */
	private static Log log = LogFactory.getLog(CartTool.class);

	/** Shopping cart storage **/
	private HashMap<Item, Integer> cart = new HashMap<>();

	/**
	 * Initialize the application tool.
	 *
	 * @param data initialization data
	 */
	@Override
	public void init(Object data) {
		cart = new HashMap<Item, Integer>();
	}

	/**
	 * Refresh the application tool.
	 */
	@Override
	public void refresh() {
		// do nothing
	}

	/**
	 * Empties the shopping cart
	 */
	public void empty() {
		cart.clear();
	}

	/**
	 * Add an item to the cart (cannot add more than inventory available)
	 * 
	 * @param item
	 * @param qty
	 */
	public void addItem(Item item, int qty) {
		this.cart.put(item, qty);
	}

	public int getQuantity(Item item) {
		if (this.cart != null)
			if (this.cart.containsKey(item))
				return this.cart.get(item);
		return 0;
	}

	/**
	 * Remove an item from the shopping cart
	 * 
	 * @param item
	 */
	public void removeItem(Item item) {
		if (this.cart != null)
			if (this.cart.containsKey(item))
				this.cart.remove(item);
	}

	public List<Item> getItems() {
		ArrayList<Item> items = new ArrayList<>();
		for (Item item : this.cart.keySet())
			items.add(item);
		return items;
	}

	public String getFormattedSubtotal() {
		try {
			DecimalFormat df = new DecimalFormat("0.00");
			String output = "$" + df.format(this.getSubtotal());
			return output;
		} catch (Exception e) {
			return "$0.00";
		}
	}

	public double getSubtotal() {
		double subtotal = 0.00;
		for (Item item : this.cart.keySet()) {
			int qty = this.cart.get(item);
			double amt = item.getPrice() * qty;
			subtotal += amt;
		}
		return subtotal;
	}

	/**
	 * Constructor does initialization stuff
	 */
	public CartTool() {
	}

}

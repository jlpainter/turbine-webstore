
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

package com.jivecast.webstore.util;

/*
 *  Present date presentations in nice format
 */
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import java.text.*;

/**
 * Description of the Class
 *
 * @author painter
 */
public class LocalDate {

	private static Locale usLocale = new Locale("en", "US");
	private static String pattern = "EEE, MMM d, yyyy K:mm a";
	private static String pattern2 = "EEE, MMM d, yyyy";

	
	// default compat with mysql date format
	private static String REPORTpattern = "MM/dd/yyyy";
	private static String SQLpattern = "yyyy-MM-dd H:mm:ss";
	private static String longSQLpattern = "yyyy-MM-dd H:mm:ss.m";
	private static String SQLpatternNoTime = "yyyy-MM-dd";

	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static String display(Date showDate) {
		if (showDate != null) {
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(pattern, usLocale);
			return formatter.format(showDate);
		} else {
			return "";
		}
	}

	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static String reportDate(Date showDate) {
		if (showDate != null) {
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(REPORTpattern, usLocale);
			String dte = formatter.format(showDate);
			return dte;
		} else {
			return "";
		}
	}
	
	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static String noticeDate(Date showDate) {
		if (showDate != null) {
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(pattern2, usLocale);
			String dte = formatter.format(showDate);
			return dte;
		} else {
			return "";
		}
	}
	

	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static String sqlDate(Date showDate) {
		if (showDate != null) {
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(SQLpattern, usLocale);
			return formatter.format(showDate);
		} else {
			return "";
		}
	}

	/**
	 * we have a sql date as a string -> give us a date object
	 */
	public static Date getSqlDate(String dte) {
		if (!StringUtils.isEmpty(dte)) {
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(longSQLpattern, usLocale);
			return formatter.parse(dte, new ParsePosition(0));
		} else {
			return null;
		}
	}

	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public static String sqlDateNoTime(Date showDate) {
		if (showDate != null) {
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat(SQLpatternNoTime, usLocale);
			return formatter.format(showDate);
		} else {
			return "";
		}
	}

    /**
     * Get date in Excel safe format
     *
     * @param dte
     * @return
     */
    public static String getExcelDate(Date dte) {
        if (dte != null) {
            // Set a calendar object with the date
            SimpleDateFormat dateFormat = new SimpleDateFormat(REPORTpattern);
            return dateFormat.format(dte);
        } else {
            return "";
        }
    }


}

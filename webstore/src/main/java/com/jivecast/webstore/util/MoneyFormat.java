
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

import java.text.*;
import java.util.Locale;

/**
 *  Description of the Class
 *
 *@author     painter
 */
public class MoneyFormat {

    private static Locale usLocale = new Locale("en", "US");


    /**
     *  Description of the Method
     *
     *@param  value  Description of the Parameter
     *@return        Description of the Return Value
     */
    public String dollarFormat(double value) {
        String pattern = "0.000";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }


    /**
     *  Description of the Method
     *
     *@param  value  Description of the Parameter
     *@return        Description of the Return Value
     */
    public String custDollarFormat(double value) {

        NumberFormat usFormat = NumberFormat.getCurrencyInstance(usLocale);
        return usFormat.format(value);
        /*
         *  String pattern = "0.00";
         *  DecimalFormat myFormatter = new DecimalFormat(pattern);
         *  return myFormatter.format(value);
         */
    }


    /**
     *  Description of the Method
     *
     *@param  value  Description of the Parameter
     *@return        Description of the Return Value
     */
    public String taxFormat(double value) {
        String pattern = "0.0000";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }


    /**
     *  Description of the Method
     *
     *@param  value  Description of the Parameter
     *@return        Description of the Return Value
     */
    public String moneyInputFormat(double value) {
        String pattern = "0.000";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

}


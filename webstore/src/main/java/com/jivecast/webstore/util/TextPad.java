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

// Logging facility
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Text padding for file exports
 *
 *@author     painter
 */
public class TextPad {

    /** Logging */
    private static Log log = LogFactory.getLog(TextPad.class);

    /*
     * create a padded number cell - pad with leading 0
     */
    public static String paddedNumberField( int inputData, int length ) {
        char[] oid = new char[length];
        String data = new Integer(inputData).toString();
        int size = data.length();
        if ( size > length  )
        {
            data = data.substring(0, length);
            for ( int i = 0; i < length; i++ ) {
                oid[i] = data.charAt(i); }

        } else {

            int startPos = length - size;
            for ( int i = 0; i < startPos; i++ ) {
                oid[i] = '0'; }

            int dataPos = 0;
            for ( int i = startPos; i < length; i++ ) {
                oid[i] = data.charAt(dataPos);
                dataPos++; }
        }
        return new String(oid);
    }


    /*
     * create a padded text cell
     * pad with ending " "
     */
    public static String paddedTextField( String data, int length ) {
        char[] oid = new char[length];
        int size = data.length();
        if ( size > length  )
        {
            data = data.substring(0, length);
            for ( int i = 0; i < length; i++ ) {
                oid[i] = data.charAt(i); }

        } else {

            for ( int i = 0; i < size; i++ )
                { oid[i] = data.charAt(i); }

            for ( int i = size; i < length; i++ ) {
                oid[i] = ' '; }
        }
        return new String(oid);
    }

}

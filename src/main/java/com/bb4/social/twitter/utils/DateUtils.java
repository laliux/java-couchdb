/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bb4.social.twitter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eduardo Zarate
 */
public class DateUtils {

    public static Date getDate(String date) {
        /*
         String LARGE_TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
         String twiDate = "Wed Aug 27 13:08:45 +0000 2008";
         new SimpleDateFormat(LARGE_TWITTER_DATE_FORMAT, Locale.ENGLISH)
         */
        final String TWITTER_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_FORMAT, Locale.ENGLISH);
        try {
            //sf.setLenient(true);
            return sf.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}

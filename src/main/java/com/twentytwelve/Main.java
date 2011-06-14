package com.twentytwelve;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by me...
 * User: raoulsson
 * Date: 6/11/11
 * Time: 7:40 PM
 */
public class Main {

    private static final Log LOGGER = LogFactory.getLog("Main");

    public static void main(String... args) {
        long start = new Date().getTime();
        for (int i = 0; i < 100; i++) {
            LOGGER.warn("hello world " + i);
        }
        long end = new Date().getTime();
        System.out.println("took = " + (end - start));
    }
}

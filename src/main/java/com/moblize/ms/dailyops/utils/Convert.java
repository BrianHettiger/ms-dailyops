package com.moblize.ms.dailyops.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Convert {
    private static Logger log = LoggerFactory.getLogger(Convert.class);

    public static float tryFloatParse(String src, float def) {
        if (null == src || "null".equals(src) || "".equals(src)) {
            return def;
        }
        try {
            return Float.parseFloat(src);
        } catch (NumberFormatException ex) {
            log.error("Invalid Float conversion on '{}' string", src);
        }
        return def;
    }

    public static float tryFloatParse(String src) {
        return tryFloatParse(src, 0.0f);
    }

    public static double tryDoubleParse(String src, double def) {
        if (null == src || "null".equals(src) || "".equals(src)) {
            return def;
        }
        try {
            return Double.parseDouble(src);
        } catch (NumberFormatException ex) {
            log.error("Invalid Double conversion on '{}' string", src);
        }
        return def;
    }

    public static double tryDoubleParse(String src) {
        return tryDoubleParse(src, 0.0d);
    }

    public static double tryDoubleParse(Number src, double def) {
        if (null == src) {
            return def;
        }
        return src.doubleValue();
    }

    public static double tryDoubleParse(Number src) {
        return tryDoubleParse(src, 0.0d);
    }

    public static long tryLongParse(String src) {
        if (null == src || "null".equals(src)) {
            return 0L;
        }
        try {
            return Long.parseLong(src);
        } catch (NumberFormatException ex) {
            log.error("Invalid Float conversion on '{}' string", src);
        }
        return 0L;
    }

    public static int tryIntParse(String src) {
        if (null == src || "null".equals(src)) {
            return 0;
        }
        try {
            return Integer.parseInt(src);
        } catch (NumberFormatException ex) {
            log.error("Invalid Integer conversion on '{}' string", src);
        }
        return 0;
    }

    public static Float ObjectToFloat(Object obj) {
        if (null != obj) {
            return (Float) (obj);
        } else {
            return null;
        }
    }
}

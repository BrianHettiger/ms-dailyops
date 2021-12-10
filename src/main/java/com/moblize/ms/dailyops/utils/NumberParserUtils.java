package com.moblize.ms.dailyops.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberParserUtils {

    public static float floatParse(String src, float def) {
        if (null == src || "null".equals(src)) {
            return def;
        }
        try {
            return Float.parseFloat(src);
        } catch (NumberFormatException ex) {
            log.error("Invalid Float conversion on '{%s}' string", src);
        }
        return def;
    }

    public static float floatParse(String src) {
        return floatParse(src, 0.0f);
    }


    public static int intParse(String src, int def) {
        if (null == src || "null".equals(src)) {
            return def;
        }
        try {
            return Integer.parseInt(src);
        } catch (NumberFormatException ex) {
            log.error("Invalid Float conversion on '{%s}' string", src);
        }
        return def;
    }

    public static int intParse(String src) {
        return intParse(src, 0);
    }
}

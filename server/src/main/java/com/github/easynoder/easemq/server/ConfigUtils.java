package com.github.easynoder.easemq.server;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/2
 * E-mail:easynoder@outlook.com
 */
public class ConfigUtils {

    public static int getIntValue(String key, int defaultValue) {
        String value = System.getProperty(key, defaultValue + "");
        return Integer.valueOf(value);
    }


    public static String getStringValue(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }
}

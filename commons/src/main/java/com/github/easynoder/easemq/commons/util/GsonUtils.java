package com.github.easynoder.easemq.commons.util;

import com.google.gson.Gson;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class GsonUtils {

    private static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }
}

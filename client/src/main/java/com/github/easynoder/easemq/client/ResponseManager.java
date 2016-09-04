package com.github.easynoder.easemq.client;

import java.util.concurrent.*;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/4
 * E-mail:easynoder@outlook.com
 */
public class ResponseManager {

    private static ConcurrentMap<Long, ResponseFuture> responseMap = new ConcurrentHashMap<Long, ResponseFuture>();

    public static void addResponseFuture(long requestId, ResponseFuture responseFuture) {
        responseMap.put(requestId, responseFuture);
    }

    public static void setResponse(long requestId, Object response) {

        if (responseMap.get(requestId) == null) {
            return ;
        }
        responseMap.get(requestId).setResponse(response);
    }

    public Object get(long requestId) {

        try {
            if (responseMap.get(requestId) == null) {
                return null;
            }
            return responseMap.get(requestId).get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

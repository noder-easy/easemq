package com.github.easynoder.easemq.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/4
 * E-mail:easynoder@outlook.com
 */
public class ResponseFuture implements Future{

    private long requestId;

    private Object response;

    public ResponseFuture(long requestId) {
        this.requestId = requestId;
    }

    public ResponseFuture setResponse(Object response) {
        this.response = response;
        return this;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return false;
    }

    public Object get() throws InterruptedException, ExecutionException {
        return response;
    }

    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // todo  模拟异步调用等待
        unit.sleep(timeout);
        System.out.println("等待 " + timeout + "秒钟!");
        return response;
    }
}

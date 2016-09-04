package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;

/**
 * Desc:
 * Author:easynoder
 * Date:16/7/10
 * E-mail:easynoder@outlook.com
 */
public class HttpMQClient implements IMQClient {

    public void start() {
        throw new UnsupportedOperationException();
    }

    public void send(byte cmdType, GenerateMessage message) {
        throw new UnsupportedOperationException();
    }

    public Object sendAndGet(byte cmdType, GenerateMessage message) {
        throw new UnsupportedOperationException();

    }

    public String localAddress() {
        throw new UnsupportedOperationException();
    }

    public String remoteAddress() {
        throw new UnsupportedOperationException();
    }

    public void close() {
        throw new UnsupportedOperationException();
    }
}

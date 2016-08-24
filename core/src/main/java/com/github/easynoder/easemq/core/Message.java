package com.github.easynoder.easemq.core;

import java.io.Serializable;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */
public class Message implements Serializable{

    public static final long serialVersionUID = 1L;

    private String head;

    private String body;

    public String getHead() {
        return head;
    }

    public Message setHead(String head) {
        this.head = head;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Message setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append("head='").append(head).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

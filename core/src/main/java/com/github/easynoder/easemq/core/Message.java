package com.github.easynoder.easemq.core;

import java.io.Serializable;
import java.util.Objects;

/**
 * Desc: 通用消息
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */
public class Message implements Serializable{

    public static final long serialVersionUID = 1L;

    private Header header;

    private String body;

    private String topic;

    public Header getHeader() {
        return header;
    }

    public Message setHeader(Header header) {
        this.header = header;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Message setBody(String body) {
        this.body = body;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Message setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append("header='").append(header).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", topic='").append(topic).append('\'');
        sb.append('}');
        return sb.toString();
    }


    public static class Header {

        private int version = 0;

        private int extra;

        public int getVersion() {
            return version;
        }

        public Header setVersion(int version) {
            this.version = version;
            return this;
        }

        public int getExtra() {
            return extra;
        }

        public Header setExtra(int extra) {
            this.extra = extra;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Header header = (Header) o;
            return version == header.version &&
                    extra == header.extra;
        }

        @Override
        public int hashCode() {
            return Objects.hash(version, extra);
        }
    }
}

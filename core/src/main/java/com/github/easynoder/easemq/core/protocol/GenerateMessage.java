package com.github.easynoder.easemq.core.protocol;

import java.util.Objects;

/**
 * Desc: 通用消息
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 * <p>
 * {header:{version:1,cmdType:1,opaque:"1",extra:1},message:{header:{topic:"easemq"},body:"mybody"}
 */
public class GenerateMessage {

    private Header header;

    private String body;

    public Header getHeader() {
        return header;
    }

    public GenerateMessage setHeader(Header header) {
        this.header = header;
        return this;
    }

    public String getBody() {
        return body;
    }

    public GenerateMessage setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GenerateMessage{");
        sb.append("header='").append(header).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }


    public static class Header {

        private String messageId;
        private String topic;

        public String getTopic() {
            return topic;
        }

        public Header setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public String getMessageId() {
            return messageId;
        }

        public Header setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Header{");
            sb.append("messageId='").append(messageId).append('\'');
            sb.append(", topic='").append(topic).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerateMessage message = (GenerateMessage) o;
        return Objects.equals(header, message.header) &&
                Objects.equals(body, message.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, body);
    }
}

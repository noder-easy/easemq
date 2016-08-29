package com.github.easynoder.easemq.core.protocol;

import java.util.Objects;

/**
 * Desc: 通用消息
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 * <p>
 * {header:{version:1,cmdType:1,topic:"easemq",extra:1},body:"mybody"}
 */
public class Message {

    private Header header;

    private String body;

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append("header='").append(header).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }


    public static class Header {

        private int version = 0;

        private int cmdType;

        private String topic;

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

        public int getCmdType() {
            return cmdType;
        }

        public Header setCmdType(int cmdType) {
            this.cmdType = cmdType;
            return this;
        }

        public String getTopic() {
            return topic;
        }

        public Header setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Header header = (Header) o;
            return version == header.version &&
                    cmdType == header.cmdType &&
                    extra == header.extra &&
                    Objects.equals(topic, header.topic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(version, cmdType, topic, extra);
        }


        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Header{");
            sb.append("version=").append(version);
            sb.append(", cmdType=").append(cmdType);
            sb.append(", topic='").append(topic).append('\'');
            sb.append(", extra=").append(extra);
            sb.append('}');
            return sb.toString();
        }
    }
}

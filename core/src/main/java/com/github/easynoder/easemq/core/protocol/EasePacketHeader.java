package com.github.easynoder.easemq.core.protocol;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/29
 * E-mail:easynoder@outlook.com
 */
public class EasePacketHeader {

    private int version = 0;

    private int cmdType;

    private String topic;

    private int extra;

    public int getVersion() {
        return version;
    }

    public EasePacketHeader setVersion(int version) {
        this.version = version;
        return this;
    }

    public int getCmdType() {
        return cmdType;
    }

    public EasePacketHeader setCmdType(int cmdType) {
        this.cmdType = cmdType;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public EasePacketHeader setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getExtra() {
        return extra;
    }

    public EasePacketHeader setExtra(int extra) {
        this.extra = extra;
        return this;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EasePacketHeader{");
        sb.append("version=").append(version);
        sb.append(", cmdType=").append(cmdType);
        sb.append(", topic='").append(topic).append('\'');
        sb.append(", extra=").append(extra);
        sb.append('}');
        return sb.toString();
    }
}

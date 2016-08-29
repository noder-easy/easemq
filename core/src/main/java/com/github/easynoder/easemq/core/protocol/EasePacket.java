package com.github.easynoder.easemq.core.protocol;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/29
 * E-mail:easynoder@outlook.com
 */
public class EasePacket {

    private EasePacketHeader header;

    private Message message;

    public EasePacketHeader getHeader() {
        return header;
    }

    public EasePacket setHeader(EasePacketHeader header) {
        this.header = header;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public EasePacket setMessage(Message message) {
        this.message = message;
        return this;
    }
}

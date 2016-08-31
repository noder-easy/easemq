package com.github.easynoder.easemq.core;

import com.github.easynoder.easemq.core.protocol.*;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/30
 * E-mail:easynoder@outlook.com
 */
public class AckUtils {

    public static EasePacket buildAckPacket(GenerateMessage.Header srcHeader, boolean isSuccess) {
        AckMessage.Header header = new AckMessage.Header().setTopic(srcHeader.getTopic()).setMessageId(srcHeader.getMessageId());
        GenerateMessage ackMessage = new AckMessage().setHeader(header).setBody("ack");

        EasePacketHeader packetHeader = new EasePacketHeader(CmdType.CMD_ACK);
        EasePacket ackPacket = new EasePacket().setHeader(packetHeader).setMessage(ackMessage);

        return ackPacket;
    }
}

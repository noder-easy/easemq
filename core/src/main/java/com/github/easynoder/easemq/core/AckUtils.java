package com.github.easynoder.easemq.core;

import com.github.easynoder.easemq.core.protocol.*;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/30
 * E-mail:easynoder@outlook.com
 */
public class AckUtils {

    public static EasePacket buildAckPacket(EasePacket packet, GenerateMessage.Header srcHeader, boolean succ) {
        // TODO: 16/9/1 时间戳优化
        AckMessage.Header header = new AckMessage.Header();
        header.setTopic(srcHeader.getTopic());
        header.setMessageId(srcHeader.getMessageId());
        header.setTimestamp(System.currentTimeMillis());

        AckMessage ackMessage = new AckMessage();
        ackMessage.setHeader(header);
        ackMessage.setSuccess(succ);

        EasePacketHeader packetHeader = new EasePacketHeader(CmdType.CMD_ACK);
        packetHeader.setOpaque(packet.getHeader().getOpaque());
        packetHeader.setExtra(packet.getHeader().getExtra());
        packetHeader.setVersion(packet.getHeader().getVersion());


        EasePacket ackPacket = new EasePacket().setHeader(packetHeader).setMessage(ackMessage);

        return ackPacket;
    }
}

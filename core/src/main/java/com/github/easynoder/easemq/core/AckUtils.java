package com.github.easynoder.easemq.core;

import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.core.protocol.EasePacketHeader;
import com.github.easynoder.easemq.core.protocol.Message;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/30
 * E-mail:easynoder@outlook.com
 */
public class AckUtils {

    public static EasePacket buildAckPacket(EasePacket packet) {
        Message.Header header = new Message.Header().setVersion(packet.getHeader().getVersion()).setCmdType(CmdType.CMD_ACK).setTopic(packet.getHeader().getTopic()).setExtra(packet.getHeader().getExtra());
        Message deliverAck = new Message().setHeader(header).setBody("ack");

        EasePacket ackPacket = new EasePacket();
        EasePacketHeader packetHeader = new EasePacketHeader().setCmdType(CmdType.CMD_ACK).setExtra(packet.getHeader().getExtra()).setVersion(packet.getHeader().getVersion()).setTopic(packet.getHeader().getTopic());
        ackPacket.setHeader(packetHeader);
        ackPacket.setMessage(deliverAck);

        return ackPacket;
    }
}

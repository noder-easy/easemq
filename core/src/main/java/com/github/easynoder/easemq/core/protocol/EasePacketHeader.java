package com.github.easynoder.easemq.core.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/29
 * E-mail:easynoder@outlook.com
 */
public class EasePacketHeader {

    private int version = 0;

    private byte cmdType;

    private int opaque;

    private int extra = 0;

    private static final AtomicInteger atomicOpaqueu = new AtomicInteger(1);

    public EasePacketHeader(byte cmdType) {
        this(cmdType, 0, atomicOpaqueu.incrementAndGet() % Integer.MAX_VALUE, 0);
    }

    private EasePacketHeader(byte cmdType, int version, int opaque, int extra) {
        this.version = version;
        this.cmdType = cmdType;
        this.opaque = opaque;
        this.extra = extra;
    }

    public int getVersion() {
        return version;
    }

    public EasePacketHeader setVersion(int version) {
        this.version = version;
        return this;
    }

    public byte getCmdType() {
        return cmdType;
    }

    public EasePacketHeader setCmdType(byte cmdType) {
        this.cmdType = cmdType;
        return this;
    }

    public int getOpaque() {
        return opaque;
    }

    public EasePacketHeader setOpaque(int opaque) {
        this.opaque = opaque;
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
        sb.append(", opaque=").append(opaque);
        sb.append(", extra=").append(extra);
        sb.append('}');
        return sb.toString();
    }
}

package com.github.easynoder.easemq.core.protocol;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/29
 * E-mail:easynoder@outlook.com
 */
public class CmdType {

    public static final byte CMD_ACK = 0x01;

    public static final byte CMD_AUTH = 0x02;

    public static final byte CMD_HEARTBEAT = 0x03;


    public static final byte CMD_STRING_MSG = 0x10;

    // TODO: 16/8/31 thrift 二进制优化
    public static final byte CMD_BYTE_MSG = 0x11;

}

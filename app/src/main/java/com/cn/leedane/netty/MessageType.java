package com.cn.leedane.netty;

/**
 * @author LeeDane
 *         2017年10月30日 16时53分
 *         version 1.0
 */
public enum MessageType {
    CONNECT_REQ((byte)1), CONNECT_SUCCESS((byte)2), CONNECT_FAIL((byte)3),
    HEARTBEAT_REQ((byte)4), HEARTBEAT_RESP((byte)5), MSG_PUSH((byte)6);

    private byte value;

    private MessageType(byte value){
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
}

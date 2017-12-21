package com.cn.leedane.netty;

import java.io.Serializable;

/**
 * @author LeeDane
 *         2017年10月30日 16时52分
 *         version 1.0
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private byte type;
    private PushMsg msg;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public PushMsg getMsg() {
        return msg;
    }

    public void setMsg(PushMsg msg) {
        this.msg = msg;
    }
}

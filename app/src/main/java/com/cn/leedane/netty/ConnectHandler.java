package com.cn.leedane.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author LeeDane
 *         2017年10月30日 16时58分
 *         version 1.0
 */
public class ConnectHandler extends SimpleChannelInboundHandler<Message> {
    //三次握手成功,发送登录验证
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Message message = new Message();
        message.setType(MessageType.CONNECT_REQ.getValue());
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("read");
        Message message = (Message) msg;
        //登录验证失败
        if(message != null && message.getType() == MessageType.CONNECT_FAIL.getValue()){
            ctx.close();
        }else if(message.getType() == MessageType.CONNECT_SUCCESS.getValue()){//登录验证成功
            System.out.println("login is ok....");
            ctx.fireChannelRead(message);
        }else{
            ctx.fireChannelRead(message);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    private Message buildMessage(byte result){
        Message msg = new Message();
        msg.setType(result);
        return msg;
    }
}

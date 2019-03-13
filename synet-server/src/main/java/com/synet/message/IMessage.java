package com.synet.message;

/**
 * 定义交互用接口IMessage
 * */
public interface IMessage<T> {
    int getSerial();
    short getCmd();
    long getSession();
    T getMessage();
}

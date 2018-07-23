package com.mrpc.core.channel;

import com.mrpc.core.message.IMessage;

import java.io.Closeable;
import java.io.Serializable;

/**
 * @author mark.z
 */
public interface IChannel extends Closeable, Serializable {

    /**
     * 获取通道ID
     *
     * @return 通道ID
     */
    String id();

    /**
     * 连接是否开启
     *
     * @return 是否开启
     */
    boolean isOpen();

    /**
     * 读取数据
     *
     * @param messageClazz 数据类型
     * @param <T>          泛型
     * @return 读取到的数据
     */
    <T extends IMessage> T read(Class<T> messageClazz);

    /**
     * 写出数据
     *
     * @param message 需要写出去的数据
     */
    void write(IMessage message);

}

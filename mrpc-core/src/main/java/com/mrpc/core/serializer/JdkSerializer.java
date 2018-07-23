package com.mrpc.core.serializer;

import com.mrpc.core.message.IMessage;

import java.io.*;

/**
 * jdk默认的序列化
 *
 * @author mark.z
 */
public class JdkSerializer implements ISerializer {

    @Override
    public <T extends IMessage> T encoder(final byte[] bytes, final Class<T> messageClass) throws IOException, ClassNotFoundException {
        final T message;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        message = (T) objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();
        return message;
    }

    @Override
    public byte[] decoder(final IMessage message) throws IOException {
        final byte[] bytes;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(message);
        bytes = outputStream.toByteArray();
        objectOutputStream.close();
        outputStream.close();
        return bytes;
    }
}

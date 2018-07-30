package com.mrpc.core.client;

import com.mrpc.core.annotation.RpcClient;
import com.mrpc.core.channel.MChannel;
import com.mrpc.core.channel.IChannel;
import com.mrpc.core.exception.MrpcException;
import com.mrpc.core.message.RequestMessage;
import com.mrpc.core.message.ResponseMessage;
import com.mrpc.core.message.ResultCode;
import com.mrpc.core.serializer.ISerializer;
import com.mrpc.core.serializer.JdkSerializer;
import com.mrpc.core.utils.InfectUtils;
import com.mrpc.core.utils.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author mark.z
 */
public final class MrpcClient implements IClient {

    private final Logger      log        = LoggerFactory.getLogger(getClass());
    private       int         threadSize = Runtime.getRuntime().availableProcessors() * 2;
    private       ISerializer serializer = new JdkSerializer();//序列化工具类
    private       long        timeout    = 5000;//超时时间(毫秒)
    private       boolean     retry      = true;//是否重试

    private AsynchronousChannelGroup group;
    private IChannel                 channel;
    private SocketAddress            socketAddress;


    @Override
    public void connect(final String address,final Integer port) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        this.connect(address,port, false);
    }

    @Override
    public void connect(final String address,final Integer port, final boolean retry) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Objects.requireNonNull(address,"通信地址address不能为空");
        Objects.requireNonNull(port,"port不能为空");
        this.group = AsynchronousChannelGroup.withFixedThreadPool(this.threadSize, Executors.defaultThreadFactory());
        final AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open(this.group);
        asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        this.retry = retry;
        this.socketAddress = new InetSocketAddress(address, port);
        try {
            asynchronousSocketChannel.connect(socketAddress).get(5, TimeUnit.SECONDS);
        } catch (final InterruptedException | TimeoutException e) {
            log.error("", e);
        } catch (final ExecutionException e) {
            log.error("连接失败",e);
            log.warn("连接失败 <-> 是否重试:{}", this.retry);
            if (this.retry) retry();

        }
        this.channel = new MChannel(asynchronousSocketChannel, this.serializer, timeout);
    }

    @Override
    public IClient threadSize(final int threadSize) {
        Validation.requireAboveNum(threadSize,0);
        this.threadSize = threadSize;
        return this;
    }

    @Override
    public IClient serializer(final ISerializer serializer) {
        Objects.requireNonNull(serializer,"序列化对象不能为空");
        this.serializer = serializer;
        return this;
    }

    @Override
    public IClient timeout(final long timeout) {
        Validation.requireAboveNum(timeout,0);
        this.timeout = timeout;
        return this;
    }

    @Override
    public <T> T getService(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        RpcClient rpcService = InfectUtils.getInterFaceAnno(clazz, RpcClient.class);
        Objects.requireNonNull(rpcService, "您要使用的服务对像注解为空");
        Objects.requireNonNull(rpcService.value(), "您要使用的服务对像注解名称为空");
        return this.getService(rpcService.value(), clazz);
    }

    @Override
    public <T> T getService(final String name, final Class<T> clazz) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(clazz);
      return  (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                final RequestMessage requestMessage = new RequestMessage();
                requestMessage.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
                requestMessage.setServerName(name);
                requestMessage.setMethodName(method.getName());
                if (args !=null && 0 != args.length) {
                    requestMessage.setArgs(args);
                    final Class[] argsClass = new Class[args.length];
                    for (int i = 0; i < args.length; i++) {
                        argsClass[i] = args[i].getClass();
                    }
                    requestMessage.setArgsClassTypes(argsClass);
                }
                final ResponseMessage responseMessage = minvoke(requestMessage);
                if (null == responseMessage) {
                    log.warn("RPC调用返回null....");
                    return null;
                }
                if (responseMessage.getResultCode() != ResultCode.SUCCESS) {
                    throw new RuntimeException(responseMessage.getErrorMessage());
                }
                return responseMessage.getResponseObject();
            }
        });
    }

    @Override
    public ResponseMessage minvoke(final RequestMessage requestMessage) {
        try {
            synchronized (channel) {
                this.channel.write(requestMessage);
                return this.channel.read(ResponseMessage.class);
            }
        } catch (final Exception e) {
            log.error("调用异常:", e);
            log.debug("是否重试:" + this.retry);
            if (e instanceof MrpcException) {
                if (!this.retry) {
                    if (this.channel.isOpen()) {
                        try {
                            this.channel.close();
                        } catch (IOException ignored) {
                        }
                    }
                    return null;
                }
                retry();
                return minvoke(requestMessage);
            }
            final ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setSeq(requestMessage.getSeq());
            responseMessage.setResultCode(ResultCode.OTHER);
            responseMessage.setErrorMessage(e.toString());
            return responseMessage;
        }
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
        this.group.shutdownNow();
    }

    private void retry() {
        try {
            TimeUnit.SECONDS.sleep(1);
            if (null != this.channel && this.channel.isOpen()) {
                this.channel.close();
            }
            log.debug("连接地址:{}", this.socketAddress.toString());
            final AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open(this.group);
            asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            asynchronousSocketChannel.connect(this.socketAddress).get(5, TimeUnit.SECONDS);
            this.channel = new MChannel(asynchronousSocketChannel, this.serializer, timeout);
        } catch (final Exception e) {
            retry();
        }
    }
}

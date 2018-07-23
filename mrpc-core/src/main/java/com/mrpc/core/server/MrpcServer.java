package com.mrpc.core.server;

import com.mrpc.core.channel.MChannel;
import com.mrpc.core.channel.IChannel;
import com.mrpc.core.exception.MrpcException;
import com.mrpc.core.message.RequestMessage;
import com.mrpc.core.message.ResponseMessage;
import com.mrpc.core.message.ResultCode;
import com.mrpc.core.serializer.ISerializer;
import com.mrpc.core.serializer.JdkSerializer;
import com.mrpc.core.utils.MThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * RPC服务端实现
 *
 * @author mark.z
 */
public final class MrpcServer implements IServer {

    private final Logger          log             = LoggerFactory.getLogger(getClass());
    private       int             threadSize      = Runtime.getRuntime().availableProcessors() * 2;
    private       ISerializer     serializer      = new JdkSerializer();//序列化工具类
    private       long            timeout         = 5000;//超时时间(毫秒)

    private       int                             port;
    private       AsynchronousChannelGroup        group;
    private       AsynchronousServerSocketChannel channel;
    private final Map<String, Object>             serverMap;

    public MrpcServer() {
        this.serverMap = new HashMap<>();
    }

    @Override
    public IServer bind(final int port) {
        this.port = port;
        return this;
    }

    @Override
    public IServer threadSize(final int threadSize) {
        if (0 < threadSize) {
            this.threadSize = threadSize;
        } else {
            log.warn("threadSize must > 0!");
        }
        return this;
    }

    @Override
    public IServer timeout(final long timeout) {
        if (0 < timeout) {
            this.timeout = timeout;
        } else {
            log.warn("timeout must > 0");
        }
        return this;
    }

    @Override
    public IServer register(final String name, final Object object) {
        Objects.requireNonNull(name, "server'name is null");
        Objects.requireNonNull(object, "server " + name + " is null");
        this.serverMap.put(name, object);
        return this;
    }

    @Override
    public IServer register(final Object object) {
        Objects.requireNonNull(object, "server is null");
        this.serverMap.put(object.getClass().getSimpleName(), object);
        return this;
    }

    @Override
    public IServer register(final Map<String, Object> serverMap) {
        Objects.requireNonNull(serverMap, "serverMap is null");
        Iterator<Map.Entry<String, Object>> iterator = serverMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            register(next.getKey(),next.getValue());
        }
        return this;
    }

    @Override
    public IServer serializer(final ISerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    @Override
    public void start() throws IOException {
        log.debug("开始启动RPC服务端......");
        this.group = AsynchronousChannelGroup.withFixedThreadPool(this.threadSize, Executors.defaultThreadFactory());
        this.channel = AsynchronousServerSocketChannel
                .open(this.group)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress("localhost", this.port));

        this.channel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(final AsynchronousSocketChannel result, final Void attachment) {
                channel.accept(null, this);
                String localAddress = null;
                String remoteAddress = null;
                try {
                    localAddress = result.getLocalAddress().toString();
                    remoteAddress = result.getRemoteAddress().toString();
                    log.debug("创建连接 {} <-> {}", localAddress, remoteAddress);
                } catch (final IOException e) {
                    log.error("", e);
                }
                final IChannel channel = new MChannel(result, serializer, timeout);
                while (channel.isOpen()) {
                    handler(channel);
                }
                log.debug("断开连接 {} <-> {}", localAddress, remoteAddress);
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                log.error("通信失败", exc);
                try {
                    close();
                } catch (final IOException e) {
                    log.error("关闭通道异常", e);
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
        this.group.shutdownNow();
    }

    private void handler(final IChannel channel) {
        try {
                final RequestMessage request = channel.read(RequestMessage.class);
                Objects.requireNonNull(request, "request is null");
                final String serverName = request.getServerName();
                final Object obj = this.serverMap.get(serverName);
                final Method method = obj.getClass().getMethod(request.getMethodName(), request.getArgsClassTypes());
                MThreadPool.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        Object response = null;
                        try {
                            response = method.invoke(obj, request.getArgs());
                        } catch (final Exception ignored) {
                        }
                        final ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setSeq(request.getSeq());
                        responseMessage.setResultCode(ResultCode.SUCCESS);
                        responseMessage.setResponseObject(response);
                        channel.write(responseMessage);
                    }
                });
        } catch (final Exception e) {
            if (e instanceof MrpcException) {
                if (channel.isOpen()) {
                    try {
                        channel.close();
                    } catch (final IOException ignored) {
                    }
                }
            }
        }
    }
}

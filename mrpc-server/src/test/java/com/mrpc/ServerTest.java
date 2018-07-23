package com.mrpc;
import com.mrpc.core.annotation.RpcService;
import com.mrpc.core.server.MrpcServer;
import com.mrpc.core.utils.InfectUtils;

/**
 * @author mark.z
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {
        new MrpcServer()
                .threadSize(20)
                .register(new TestService())
                .bind(4567)
                .start();
    }
}

package com.mrpc;
import com.mrpc.core.server.FastRpcServer;

/**
 * @author mark.z
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {
        new FastRpcServer()
                .threadSize(20)
                .register("test", new TestService())
                .bind(4567)
                .start();
    }
}

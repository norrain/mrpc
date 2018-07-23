package com.mrpc.client;

import com.mrpc.core.test.po.User;
import com.mrpc.core.client.MrpcClient;
import com.mrpc.core.client.IClient;

import java.net.InetSocketAddress;

/**
 * @author mark.z
 */
public class ClientTest {

    public static void main(String[] args) {
        try{
            IClient client = new MrpcClient();
            client.connect(new InetSocketAddress("127.0.0.1", 4567));
            ITestService service = client.getService("test", ITestService.class);
            String say = service.say("Hello!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println(service.name());
            User user = new User();
            user.setAge(11);
            user.setName("norrain");
            user.setSex(true);
            System.out.println(service.doUser(user));
            System.out.println(say);
            service.ok("aaaaaaaaaaaaaaaaa");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.mrpc.client;

import com.mrpc.core.client.MrpcClient;
import com.mrpc.core.client.IClient;
import com.mrpc.test.client.ITestService;
import com.mrpc.test.po.User;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * @author mark.z
 */
public class ClientTest {
    //TODO 添加没有服务对象错误处理
    public static void main(String[] args) {
        int errorCount =0;
        long time = new Date().getTime();
        for (int i = 0; i < 1000 ; i++) {
            try{
                    IClient client = new MrpcClient();
                    client.connect(new InetSocketAddress("localhost", 4567));
                    ITestService service = client.getService(ITestService.class);
                    String say = service.say("Hello!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println(service.name());
                    User user = new User();
                    user.setAge(11);
                    user.setName("norrain");
                    user.setSex(true);
                    System.out.println(service.doUser(user));
                    System.out.println(say);
                    service.ok("aaaaaaaaaaaaaaaaa");
                    client.close();
                    Thread.sleep(10);
            } catch (Exception e) {
                errorCount++;
                e.printStackTrace();
            }
        }
        System.out.println(errorCount+"==="+(new Date().getTime() - time));
    }
}

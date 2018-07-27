package com.mrpc.client;

import com.mrpc.core.client.MrpcClient;
import com.mrpc.core.client.IClient;
import com.mrpc.test.client.ITestService;
import com.mrpc.test.po.User;

import java.util.Date;

/**
 * @author mark.z
 */
public class ClientTest {
    //TODO 添加没有服务对象错误处理
    public static void main(String[] args) {
        int loopNum =100;
        long time = new Date().getTime();
        try(IClient client = new MrpcClient()){
            client.connect("127.0.0.1", 4567);
            ITestService service = client.getService(ITestService.class);
            for (int i = 0; i < loopNum ; i++) {
                System.out.println(service.name());
                System.out.println(service.doUser(new User("mark.z_"+i,i,false)));
                System.out.println(service.say("Hello World!"));
                service.ok("TEST MARK");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("===总共耗时==="+(new Date().getTime() - time));
    }
}

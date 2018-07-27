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
        int errorCount =0;
        long time = new Date().getTime();
            try{
                final IClient client = new MrpcClient();
                 client.connect("127.0.0.1", 4567);
                final ITestService service = client.getService(ITestService.class);
                for (int i = 0; i < 100 ; i++) {
                            System.out.println(service.name());
                            System.out.println(service.doUser(new User("mark.z",11,false)));
                            System.out.println(service.say("Hello World!"));
                            service.ok("aaaaaaaaaaaaaaaaa");
                }
                 client.close();
            } catch (Exception e) {
                errorCount++;
                e.printStackTrace();
            }

        System.out.println(errorCount+"==="+(new Date().getTime() - time));
    }
}

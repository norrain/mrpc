package com.mrpc.client;

import com.mrpc.core.client.MrpcClient;
import com.mrpc.core.client.IClient;
import com.mrpc.core.utils.MThreadPool;
import com.mrpc.test.client.ITestService;
import com.mrpc.test.po.User;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发测试
 * @author mark.z
 */
public class ClientThreadTest {
    //TODO 添加没有服务对象错误处理
    public static void main(String[] args) {
        int loopNum = 10000;
        final  long time = new Date().getTime();
            try{
                final IClient client = new MrpcClient();
                client.connect("127.0.0.1", 4567);
                final ITestService service = client.getService(ITestService.class);
                final CountDownLatch countDownLatch = new CountDownLatch(loopNum);
                for (int i = 0; i < loopNum ; i++) {
                    final int aa = i+1;
                    MThreadPool.runInThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(aa+"=="+service.doUser(new User("mark.z",aa,false)));
                            countDownLatch.countDown();
                        }
                    });
                }
                countDownLatch.await();
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
                System.out.println("花费时间:"+(new Date().getTime() - time));
    }
}

package com.mrpc.test.client;

import com.mrpc.core.annotation.RpcClient;
import com.mrpc.test.po.User;

/**
 * @author mark.z
 */
@RpcClient("TEST")
public interface ITestService {

    String say(String what);

    String name();

    void ok(String ok);

    void none();

    User doUser(User user);
}

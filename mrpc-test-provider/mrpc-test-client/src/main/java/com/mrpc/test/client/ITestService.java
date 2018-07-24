package com.mrpc.test.client;

import com.mrpc.core.annotation.RpcService;
import com.mrpc.test.po.User;

/**
 * @author mark.z
 */
@RpcService("TEST")
public interface ITestService {

    String say(String what);

    String name();

    void ok(String ok);

    void none();

    User doUser(User user);
}

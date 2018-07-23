package com.mrpc;

import com.mrpc.core.annotation.RpcService;
import com.mrpc.core.test.po.User;

/**
 * @author mark.z
 */
@RpcService("TTTTT")
public interface ITestService {

    String say(String what);

    String name();

    void ok(String ok);

    void none();

    User doUser(User user);
}

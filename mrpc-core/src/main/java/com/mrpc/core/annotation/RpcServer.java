package com.mrpc.core.annotation;

import java.lang.annotation.*;

/**
 * 服务实现注册注解
 * @author mark.z
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServer {

    String value() default "";
}

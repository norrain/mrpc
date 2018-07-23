package com.mrpc.core.annotation;

import java.lang.annotation.*;

/**
 * 服务注册注解
 * @author mark.z
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {

    String value() default "";
}

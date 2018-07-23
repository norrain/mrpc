package com.mrpc.core.utils;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * 反射功具类
 * @Author mark.z
 */
public class InfectUtils {

    /**
     * 获取对象实现的接口  注册的服务注解对像
     * @param target 类
     * @param anno 注解
     * @param <T>
     * @return
     */
    public static <T> T getInterFaceAnno(Object target,Class<? extends Annotation> anno)
    {
        Objects.requireNonNull(target,"target为空");
        System.out.println(target instanceof Class);
        if (target.getClass().isInterface())
            return (T)target.getClass().getAnnotation(anno);

        Class<?>[] interfaces = target.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Annotation annotation = interfaces[i].getAnnotation(anno);
            if (annotation!=null){
                return (T)annotation;
            }
        }

        return null;
    }

    /**
     * 获取对象实现的接口  注册的服务注解对像（接口）
     * @param target 接口
     * @param anno
     * @param <T>
     * @return
     */
    public static <T> T getInterFaceAnno(Class target,Class<? extends Annotation> anno)
    {
        Objects.requireNonNull(target,"target为空");

        return (T)target.getAnnotation(anno);
    }


}

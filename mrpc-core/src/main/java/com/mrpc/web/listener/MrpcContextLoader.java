package com.mrpc.web.listener;

import com.mrpc.core.annotation.RpcServer;
import com.mrpc.core.server.MrpcServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 初始化注册所有服务
 * @Author mark.z
 */
@Component
public class MrpcContextLoader implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcServer.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcServer.class).value();
                //注册所有服务
                new MrpcServer().register(interfaceName,serviceBean);
            }
        }
    }
}

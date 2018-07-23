package com.mrpc.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mark.z
 */
public class MThreadPool {

    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static void runInThread(Runnable runnable){
        cachedThreadPool.execute(runnable);
    }
}

package com.moonyue.chapter5.twinsLock;

import com.moonyue.chapter4.SleepUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class TwinsLockTest {
    public static void main(String[] argv){
        final Lock lock = new TwinsLock();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 2000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100, true));

        for(int i = 0; i < 10; i ++){
            executor.submit(()->{
                while (true){
                    lock.lock();
                    try{
                        SleepUtils.second(1);
                        System.out.println(Thread.currentThread().getName());
                        SleepUtils.second(1);
                    }finally {
                        lock.unlock();
                    }
                }
            });
        }
    }
}

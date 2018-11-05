package com.moonyue;

import com.moonyue.chapter4.MoonYueHttpServer;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class Main {

    public static void main(String[] args) throws Exception{
	// write your code here
//        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
//        for(ThreadInfo threadInfo : threadInfos){
//            System.out.println(threadInfo.getThreadId() + "---" + threadInfo.getThreadName());
//        }
        MoonYueHttpServer.setPort(5200);
        MoonYueHttpServer.setBasePath("/home/muyue/Downloads");
        MoonYueHttpServer.start();
    }
}

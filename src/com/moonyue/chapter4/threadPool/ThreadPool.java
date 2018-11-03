package com.moonyue.chapter4.threadPool;

public interface ThreadPool<Job extends Runnable> {
    void execute(Job worker);

    void shutdown();

    void addWorkers(int num);

    void removeWorkers(int num);

    int getWorkerSize();
}

package com.moonyue.chapter4.threadPool.impl;

import com.moonyue.chapter4.threadPool.ThreadPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class MoonYueThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    private static final int MAX_WORKER_NUMBERS = 10;
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    private static final int MIN_WORKER_NUMBERS = 1;
    private final LinkedList<Job> jobs = new LinkedList<>();

    private final List<Worker> aliveWorkers = Collections.synchronizedList(new ArrayList<>());

    private int workerNum = DEFAULT_WORKER_NUMBERS;
    private AtomicLong threadNum = new AtomicLong();

    public MoonYueThreadPool(){
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public MoonYueThreadPool(int num){
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);
    }

    private void initializeWorkers(int num){
        for(int i = 0; i < num; i ++){
            Worker worker = new Worker();
            aliveWorkers.add(worker);
            Thread thread = new Thread(worker, "MoonYueThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    class Worker implements Runnable{
        private volatile boolean running = true;

        @Override
        public void run(){
            while (running){
                Job job = null;
                synchronized (jobs){
                    while (jobs.isEmpty()){
                        try{
                            jobs.wait();
                        }catch (InterruptedException e){
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                }
               if(Objects.nonNull(job)){
                   try {
                       job.run();
                   }catch (Exception e){

                   }
               }
            }

        }

        public void shutdown(){
            running = false;
        }
    }
    @Override
    public void execute(Job job) {
        if(Objects.nonNull(job)){
            synchronized (jobs){
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }


    @Override
    public void shutdown() {
        for(Worker worker : aliveWorkers){
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs){
            if(num + this.workerNum > MAX_WORKER_NUMBERS){
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorkers(int num) {
        synchronized (jobs){
            if(num > this.workerNum){
                throw new IllegalArgumentException("beyond workNum");
            }
            int count = 0;
            while (count < num){
                Worker worker = aliveWorkers.get(count);
                if(aliveWorkers.remove(worker)){
                    worker.shutdown();
                    count ++;
                }
            }
            this.workerNum -= count;
        }
    }

    @Override
    public int getWorkerSize() {
        return jobs.size();
    }
}

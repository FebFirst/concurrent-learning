package com.moonyue.chapter1;

public class ConcurrentTest {
    private static final long COUNT = 100000001;

    public static void main(String[] argv) throws InterruptedException{
        conccurency();
        serial();
    }

    private static void conccurency() throws InterruptedException{
        long start = System.currentTimeMillis();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int a = 0;
                for(long i = 0 ; i< COUNT; i ++){
                    a += 5;
                }
            }
        });

        thread.start();
        int b = 0;
        for(long i = 0; i < COUNT; i ++){
            b --;
        }

        thread.join();

        long time = System.currentTimeMillis() - start;

        System.out.println("Concurrency: " + time+ "ms, b = " + b);
    }

    private static void serial(){
        long start = System.currentTimeMillis();
        int a = 0;
        for(long i = 0 ; i< COUNT; i ++){
            a += 5;
        }
        int b = 0;
        for(long i = 0; i < COUNT; i ++){
            b --;
        }
        long time = System.currentTimeMillis() - start;

        System.out.println("Serial: " + time+ "ms, b = " + b + ", a = " + a);
    }
}

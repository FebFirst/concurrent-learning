package com.moonyue.chapter4.connectionPoll;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Objects;

public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int size){
        if(size > 0){
            for(int i = 0; i < size; i++){
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public void releaseConnection(Connection connection){
        if(Objects.nonNull(connection)){
            synchronized (pool){
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    public Connection fetchConnnection(long mills) throws InterruptedException{
        synchronized (pool){
            if(mills < 0){
                while (pool.isEmpty()){
                    pool.wait();
                }
                return pool.removeFirst();
            }else{
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0){
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection res = null;
                if(!pool.isEmpty()){
                    res = pool.removeFirst();
                }
                return res;
            }
        }
    }
}

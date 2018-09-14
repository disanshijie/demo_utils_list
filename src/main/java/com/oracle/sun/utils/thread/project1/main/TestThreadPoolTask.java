package com.oracle.sun.utils.thread.project1.main;

import java.util.concurrent.TimeUnit;

public class TestThreadPoolTask implements Runnable {
	 
    private int id ;
    private String name;
 
    public TestThreadPoolTask(int id, String name) {
        this.id = id;
        this.name = name;
    }
 
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ",id:" +this.id);
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

}

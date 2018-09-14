package com.oracle.sun.utils.thread.project1.main;

import java.util.concurrent.ThreadPoolExecutor;

import com.oracle.sun.utils.thread.project1.ThreadPoolExecutorFactory;
import com.oracle.sun.utils.thread.project1.ThreadPoolExecutorFactory2;

public class Test {

	
	public static void main(String[] arg0) {
		test2();

	}
	
	public static void test1() {
		for (int i = 1; i <= 20 ;i++){
            TestThreadPoolTask task = new TestThreadPoolTask(i,"name"+i);
            ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(task);
        }
	}
	
	public static void test2() {
		TestThreadPoolTask task = new TestThreadPoolTask(1,"name"+1);
		ThreadPoolExecutor threadPoolExecutor=ThreadPoolExecutorFactory2.getThreadPoolExecutor();
		threadPoolExecutor.execute(task);
	}
	
}

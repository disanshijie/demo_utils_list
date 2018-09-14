package com.oracle.sun.utils.thread.project1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
 
/**
 * 处理内容:线程池工厂类
 * 懒汉模式
 */
public class ThreadPoolExecutorFactory {
	/**
	 * corePoolSize 池中所保存的线程数，包括空闲线程。  
	 */
	private static final int corePoolSize = 40;
	/**
	 * maximumPoolSize - 池中允许的最大线程数(采用LinkedBlockingQueue时没有作用)。  
	 */
	private static final int maximumPoolSize = 40;
	/**
	 * keepAliveTime -当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间，线程池维护线程所允许的空闲时间
	 * 	线程池中线程的最大空闲时间，超过这个时间空闲线程将被回收
	 * 	TimeUnit.SECONDS, 秒
	 * 	TimeUnit.MILLISECONDS,毫秒
	 */
	private static final int keepAliveTime = 60;
	
	/**
	 * 执行前用于保持任务的队列（缓冲队列）
	 */
	private static final int capacity = 300;
	
	/**
	 * 线程池对象
	 */
	private static ThreadPoolExecutor threadPoolExecutor = null;
	
	//构造方法私有化
	private ThreadPoolExecutorFactory(){}
	
	public static ThreadPoolExecutor getThreadPoolExecutor(){
		if(null == threadPoolExecutor){
			ThreadPoolExecutor t;
			synchronized (ThreadPoolExecutor.class) {
				t = threadPoolExecutor;
				if(null == t){
					synchronized (ThreadPoolExecutor.class) {
						
						ArrayBlockingQueue<Runnable> arrayWorkQueue = new ArrayBlockingQueue(10);
					    LinkedBlockingDeque<Runnable> linkedWorkQueue = new LinkedBlockingDeque();
					    
						t = new ThreadPoolExecutor(corePoolSize,
								maximumPoolSize,
								keepAliveTime,
								TimeUnit.SECONDS,
								
								//下面是采用有界队列和无界队列的区别
								//arrayWorkQueue,
								linkedWorkQueue,
								
								//下面是jdk的四种执行策略
								
								//这种策略直接抛出异常，丢弃任务。
				                //new ThreadPoolExecutor.AbortPolicy()
								//这种策略和AbortPolicy几乎一样，也是丢弃任务，只不过他不抛出异常。
				                //new ThreadPoolExecutor.DiscardPolicy() 
								//线程调用运行该任务的 execute 本身。此策略提供简单的反馈控制机制，能够减缓新任务的提交速度。没看明白，当时是我的main线程执行的task5
				                //new ThreadPoolExecutor.CallerRunsPolicy() 
								
								//如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）
				                new ThreadPoolExecutor.DiscardOldestPolicy()
								);
					}
					threadPoolExecutor = t;
				}
			}
		}
		return threadPoolExecutor;
	}

}

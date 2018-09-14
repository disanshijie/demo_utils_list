

ThreadPoolExecutor执行任务时的心路历程是什么样的呢？（以下用currentSize表示线程池中当前线程数量）
（1）当currentSize<corePoolSize时，没什么好说的，直接启动一个核心线程并执行任务。

（2）当currentSize>=corePoolSize、并且workQueue未满时，添加进来的任务会被安排到workQueue中等待执行。

（3）当workQueue已满，但是currentSize<maximumPoolSize时，会立即开启一个非核心线程来执行任务。

（4）当currentSize>=corePoolSize、workQueue已满、并且currentSize>maximumPoolSize时，调用handler默认抛出RejectExecutionExpection异常。
============================================================
无界队列 如LinkedBlockingQueue
有界队列 如ArrayBlockingQueue

1：如果自定义队列是有界队列:
当线程池中的任务数 > maximumPoolSize时，线程池会自动创建线程,直到 线程数 = maximumPoolSize；
如果这个时候线程池任务还 > maximumPoolSize, 线程池就会把任务放到任务队列中；
如果队列满了采用配置的拒绝策略
2：如果自定义队列是无解队列：
 maximumPoolSize 参数无效，线程池中就只有corePoolSize个线程执行任务。知道系统崩溃（内存溢出，由于是无解队列）
总结
使用无界队列，要防止任务增长的速度远远超过处理任务的速度，控制不好可能导致的结果就是内存溢出。

使用有界队列，关键在于调节线程数和Queue大小 ，线程数多，队列容量少，资源浪费。线程数少，队列容量多，性能低，还可能导致内存溢出。
 
3：拒绝策略建议采用自定义拒绝策略

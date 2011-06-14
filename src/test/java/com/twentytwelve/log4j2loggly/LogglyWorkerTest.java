package com.twentytwelve.log4j2loggly;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by me...
 * User: raoulsson
 * Date: 6/13/11
 * Time: 1:47 AM
 */
//@Ignore
public class LogglyWorkerTest extends TestCase {

    public static final String MY_KEY = "your-key";
    public static final String LOGGLY_HTTP_INPUT = "https://logs.loggly.com/inputs/";
    private BlockingQueue<String> work = new ArrayBlockingQueue<String>(50);

    /**
     * Works
     *
     * @throws InterruptedException
     */
    @Test
    public void testOnce() throws InterruptedException {
        LogglyWorker worker1 = new LogglyWorker(work, LOGGLY_HTTP_INPUT, MY_KEY);
        new Thread(worker1, "worker1").start();
        work.put("hello world...");
        while (!work.isEmpty() || worker1.isWorking()) {
            Thread.sleep(100);
        }
    }

    /**
     * Fails after first job is done
     *
     * @throws InterruptedException
     */
    @Test
    public void testWithOneWorker() throws InterruptedException {
        LogglyWorker worker1 = new LogglyWorker(work, LOGGLY_HTTP_INPUT, MY_KEY);
        new Thread(worker1, "worker1").start();
        for (int i = 0; i < 5; i++) {
            work.put("hello world... " + i);
        }
        while (!work.isEmpty() || worker1.isWorking()) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testWithMultipleWorkers() throws InterruptedException {
        LogglyWorker worker1 = new LogglyWorker(work, LOGGLY_HTTP_INPUT, MY_KEY);
        new Thread(worker1, "worker1").start();
        LogglyWorker worker2 = new LogglyWorker(work, LOGGLY_HTTP_INPUT, MY_KEY);
        new Thread(worker2, "worker2").start();
        LogglyWorker worker3 = new LogglyWorker(work, LOGGLY_HTTP_INPUT, MY_KEY);
        new Thread(worker3, "worker3").start();
        LogglyWorker worker4 = new LogglyWorker(work, LOGGLY_HTTP_INPUT, MY_KEY);
        new Thread(worker4, "worker4").start();
        for (int i = 0; i < 50; i++) {
            work.put("hello world... " + i);
        }
        while (!work.isEmpty() || worker1.isWorking() || worker2.isWorking() || worker3.isWorking() || worker4.isWorking()) {
            Thread.sleep(100);
        }
    }
}

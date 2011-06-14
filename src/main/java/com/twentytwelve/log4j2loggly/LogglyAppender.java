package com.twentytwelve.log4j2loggly;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by me...
 * User: raoulsson
 * Date: 6/12/11
 * Time: 12:01 PM
 */
public class LogglyAppender extends AppenderSkeleton {

    private BlockingQueue<String> workQueue;

    private int threadCount = 4;
    private int queueSize = 50;
    private String httpsProxyHost;
    private Integer httpsProxyPort;
    private String logglyUrl;
    private String logglyKey;

    public LogglyAppender() {
        this(null);
    }

    public LogglyAppender(int threadCount, int queueSize, String httpsProxyHost, Integer httpsProxyPort, String logglyUrl, String logglyKey) {
        this.threadCount = threadCount;
        this.queueSize = queueSize;
        this.httpsProxyHost = httpsProxyHost;
        this.httpsProxyPort = httpsProxyPort;
        this.logglyUrl = logglyUrl;
        this.logglyKey = logglyKey;
        activateOptions();
    }

    public void activateOptions() {
        if (httpsProxyHost != null) {
            System.setProperty("https.proxyHost", httpsProxyHost);
        }
        if (httpsProxyPort != null) {
            System.setProperty("https.proxyPort", httpsProxyPort + "");
        }
        workQueue = new ArrayBlockingQueue<String>(queueSize);
        for (int i = 1; i <= threadCount; i++) {
            Thread t = new Thread(new LogglyWorker(workQueue, logglyUrl, logglyKey), "loggly-worker-" + i);
            t.start();
        }
        System.out.println(this.toString());
    }

    public LogglyAppender(Layout layout) {
        setLayout(layout);
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        String format = layout.format(loggingEvent);
        try {
            workQueue.put(format);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void close() {
        System.out.println("Shutting down loggly with unfinished item size: " + workQueue.size());
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public void setHttpsProxyHost(String httpsProxyHost) {
        this.httpsProxyHost = httpsProxyHost;
    }

    public void setHttpsProxyPort(int httpsProxyPort) {
        this.httpsProxyPort = httpsProxyPort;
    }

    public void setLogglyUrl(String logglyUrl) {
        this.logglyUrl = logglyUrl;
    }

    public void setLogglyKey(String logglyKey) {
        this.logglyKey = logglyKey;
    }

    @Override
    public String toString() {
        return "LogglyAppender{" +
                "threadCount=" + threadCount +
                ", queueSize=" + queueSize +
                ", httpsProxyHost='" + httpsProxyHost + '\'' +
                ", httpsProxyPort=" + httpsProxyPort +
                ", logglyUrl='" + logglyUrl + '\'' +
                ", logglyKey='" + logglyKey + '\'' +
                '}';
    }
}

package com.twentytwelve.log4j2loggly;

import java.io.*;
import java.net.*;
import java.security.Security;
import java.util.concurrent.BlockingQueue;

/**
 * Created by me...
 * User: raoulsson
 * Date: 6/12/11
 * Time: 7:23 PM
 */
public class LogglyWorker implements Runnable {

    private final String logglyUrl;
    private final String logglyKey;

    private volatile boolean working = false;
    private BlockingQueue<String> work;

    public LogglyWorker(BlockingQueue<String> work, String logglyUrl, String logglyKey) {
        this.logglyUrl = logglyUrl;
        this.logglyKey = logglyKey;
        this.work = work;

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        if (System.getProperty("java.protocol.handler.pkgs") == null) {
            System.getProperties().setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        } else {
            System.getProperties().setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol|".concat(System.getProperty("java.protocol.handler.pkgs")));
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                sendLog(work.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendLog(String data) {
        working = true;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        try {
            URL url = new URL(logglyUrl + logglyKey);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(2000);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(data);
            dos.flush();

            int responseCode = conn.getResponseCode();
            System.out.println(Thread.currentThread().getName() + ": " + logglyUrl + ": responseCode = " + responseCode);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
            working = false;
        }
    }

    public boolean isWorking() {
        return working;
    }
}
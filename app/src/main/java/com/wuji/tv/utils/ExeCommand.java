package com.wuji.tv.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Runtime.getRuntime;


public class ExeCommand {
    private Process process;
    private BufferedReader successResult;
    private BufferedReader errorResult;
    private DataOutputStream os;
    private boolean bSynchronous;
    private boolean bRunning = false;
    ReadWriteLock lock = new ReentrantReadWriteLock();
    private StringBuffer result = new StringBuffer();


    public ExeCommand(boolean synchronous) {
        bSynchronous = synchronous;
    }


    public ExeCommand() {
        bSynchronous = true;
    }


    public boolean isRunning() {
        return bRunning;
    }


    public String getResult() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return new String(result);
        } finally {
            readLock.unlock();
        }
    }

    public ExeCommand run(String command, final int maxTime) {
        Log.i("cyb", "run command:" + command + ",maxtime:" + maxTime);
        if (command == null || command.length() == 0) {
            return this;
        }

        try {
            process = getRuntime().exec("sh");
        } catch (Exception e) {
            return this;
        }
        bRunning = true;
        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        os = new DataOutputStream(process.getOutputStream());

        try {
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            os.close();
            if (maxTime > 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(maxTime);
                        } catch (Exception e) {
                        }
                        try {
                            int ret = process.exitValue();
                        } catch (IllegalThreadStateException e) {
                            Log.i("cyb", "take maxTime,forced to destroy process");
                            process.destroy();
                        }
                    }
                }).start();
            }

            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = successResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        Log.i("cyb", "read InputStream exception:" + e.toString());
                    } finally {
                        try {
                            successResult.close();
                        } catch (Exception e) {
                            Log.i("cyb", "close InputStream exception:" + e.toString());
                        }
                    }
                }
            });
            t1.start();

            final Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = errorResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        Log.i("cyb", "read ErrorStream exception:" + e.toString());
                    } finally {
                        try {
                            errorResult.close();
                        } catch (Exception e) {
                            Log.i("cyb", "read ErrorStream exception:" + e.toString());
                        }
                    }
                }
            });
            t2.start();

            Thread t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        t1.join();
                        t2.join();
                        process.waitFor();
                    } catch (Exception e) {

                    } finally {
                        bRunning = false;
                        Log.i("cyb", "run command process end");
                    }
                }
            });
            t3.start();

            if (bSynchronous) {
                t3.join();
            }
        } catch (Exception e) {
            Log.i("cyb", "run command process exception:" + e.toString());
        }
        return this;
    }

}
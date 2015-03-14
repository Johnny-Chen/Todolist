package com.liujiang.todolist;

/**
 * Created by king on 15-3-14.
 */
public class Sync {
    static byte[] lock= new byte[0];
    final static int TIME_OUT = -1;
    final static int RESULT_OK = 0;
    final static int RESULT_ERROR = 1;

    static int result = TIME_OUT;

    public static int waitForResult(int milis) {

        synchronized (lock) {
            result = TIME_OUT;
            try {
                lock.wait(milis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void notifyOK() {

        synchronized (lock) {
            result = RESULT_OK;
            lock.notify();
        }
    }

    public static void notifyError() {
        synchronized (lock) {
            result = RESULT_ERROR;
            lock.notify();
        }
    }
}

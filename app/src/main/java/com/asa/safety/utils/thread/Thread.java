package com.asa.safety.utils.thread;

import android.os.Handler;

public abstract class Thread {
    protected Handler handler;
    protected Runnable runnable;

    public Thread() {
        handler = new Handler();
        setRunnable();
    }

    public void startThread() {
        handler.post(runnable);
    }

    public void setRunnable() {
        runnable = this::task;
    }

    public abstract void task();
}

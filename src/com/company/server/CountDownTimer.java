package com.company.server;

import java.util.Timer;
import java.util.TimerTask;

public abstract class CountDownTimer {
    private final long delay;
    private final TimerTask task;
    private final Timer timer;

    public CountDownTimer(long delay) {
        this.delay = delay;
        task = new TimerTask() {
            @Override
            public void run() {
                onFinish();
            }
        };
        timer = new Timer();
    }

    public abstract void onFinish();

    public void cancel() {
        task.cancel();
        timer.cancel();
    }

    public void run() {
        if (delay > 0) {
            timer.schedule(task, delay);
        }
    }
}

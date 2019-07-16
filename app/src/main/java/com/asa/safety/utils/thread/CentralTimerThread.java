package com.asa.safety.utils.thread;

import android.util.Log;

import com.asa.safety.utils.thread.event.TimerEvent;

import java.util.ArrayList;
import java.util.List;

public class CentralTimerThread extends Thread {
    private List<TimerEvent> timerEventList;
    protected int currentTime;
    protected int alarmingTime;

    //start alarm again when true
    protected boolean again = true;
    private final int timeUnit = 1000;

    public CentralTimerThread() {
        super();
        setAlarmingTime(1);
        currentTime = -1;
        timerEventList = new ArrayList<>();
    }

    protected void setAlarmingTime(int time) {
        this.alarmingTime = time * timeUnit;
    }

    @Override
    public void task() {
        currentTime++;
        triggerEvents();
        if (again) {
            handler.postDelayed(runnable, alarmingTime);
        }
    }

    private void triggerEvents() {
        Log.e("CentralTimerThread", "Trigger");
        for (int i=0; i<timerEventList.size(); i++) {
            timerEventList.get(i).triggerEvent(currentTime);
        }
    }

    public void applyTimerEvent(TimerEvent timerEvent) {
        timerEvent.setCentralTimerInitTime(currentTime);

        timerEventList.add(timerEvent);
    }
}

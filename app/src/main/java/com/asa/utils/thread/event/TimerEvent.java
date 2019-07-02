package com.asa.utils.thread.event;

public abstract class TimerEvent {
    protected int centralTimerInitTime;
    protected int alarmingTime;
    protected boolean isTriggerNow;

    protected String eventName = "TimerEvent";

    public TimerEvent(int alarmingTime, boolean isTriggerNow) {
        this.alarmingTime = alarmingTime;
        this.isTriggerNow = isTriggerNow;
    }

    public void triggerEvent(int currentTime) {
        if (isAvaliableToTrigger(currentTime)) {
            event();
        }
    }

    private boolean isAvaliableToTrigger(int currentTime) {
        return (currentTime-centralTimerInitTime) % alarmingTime == 0;
    }

    protected abstract void event();

    public void setCentralTimerInitTime(int time) {
        if (isTriggerNow)
            this.centralTimerInitTime = time + 1;
        else
            this.centralTimerInitTime = time;
    }
}
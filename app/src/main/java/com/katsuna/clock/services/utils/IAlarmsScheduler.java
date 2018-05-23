package com.katsuna.clock.services.utils;

import com.katsuna.clock.data.Alarm;

public interface IAlarmsScheduler {

    void schedule(CallBack callBack);

    void reschedule(Alarm alarm);

    void setAlarm(Alarm alarm);

    void snooze(Alarm alarm, long delay);

    void cancel(Alarm alarm);

    interface CallBack {
        void schedulingFinished();

        void schedulingFailed(Exception ex);
    }
}

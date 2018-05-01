package com.katsuna.clock.services.utils;

import com.katsuna.clock.data.Alarm;

public interface IAlarmScheduler {

    void schedule(CallBack callBack);

    void reschedule(Alarm alarm);

    interface CallBack {
        void schedulingFinished();
    }
}

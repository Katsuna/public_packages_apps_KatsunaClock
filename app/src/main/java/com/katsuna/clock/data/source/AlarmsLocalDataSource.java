package com.katsuna.clock.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.util.AppExecutors;

import java.util.List;


/**
 * Concrete implementation of a data source as a db.
 */
public class AlarmsLocalDataSource implements AlarmsDataSource {

    private static volatile AlarmsLocalDataSource INSTANCE;

    private final AlarmsDao mAlarmsDao;

    private final AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private AlarmsLocalDataSource(@NonNull AppExecutors appExecutors,
                                  @NonNull AlarmsDao alarmsDao) {
        mAppExecutors = appExecutors;
        mAlarmsDao = alarmsDao;
    }

    public static AlarmsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                    @NonNull AlarmsDao alarmsDao) {
        if (INSTANCE == null) {
            synchronized (AlarmsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AlarmsLocalDataSource(appExecutors, alarmsDao);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

    /**
     * Note: {@link LoadAlarmsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getAlarms(@NonNull final LoadAlarmsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Alarm> alarms = mAlarmsDao.getAlarms();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (alarms.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onAlarmsLoaded(alarms);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link GetAlarmCallback#onDataNotAvailable()} is fired if the {@link Alarm} isn't
     * found.
     */
    @Override
    public void getAlarm(final long alarmId, @NonNull final GetAlarmCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Alarm alarm = mAlarmsDao.getAlarmById(alarmId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (alarm != null) {
                            callback.onAlarmLoaded(alarm);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public Alarm getAlarm(long alarmId) {
        return mAlarmsDao.getAlarmById(alarmId);
    }

    @Override
    public void saveAlarm(@NonNull final Alarm alarm) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                if (alarm.getAlarmId() == 0) {
                    long alarmId = mAlarmsDao.insertAlarm(alarm);
                    alarm.setAlarmId(alarmId);
                } else {
                    mAlarmsDao.updateAlarm(alarm);
                }
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteAlarm(final long alarmId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mAlarmsDao.deleteAlarmById(alarmId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

}

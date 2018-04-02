package com.katsuna.clock.util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Allow instant execution of tasks.
 */
public class SingleExecutors extends AppExecutors {
    private static final Executor instant = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            command.run();
        }
    };

    public SingleExecutors() {
        super(instant, instant, instant);
    }
}

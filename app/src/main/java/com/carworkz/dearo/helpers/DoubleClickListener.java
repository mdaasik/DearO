package com.carworkz.dearo.helpers;

import android.os.Handler;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Farhan on 16/8/17.
 */

public abstract class DoubleClickListener implements View.OnClickListener {

    private Timer timer = null;
    private static final int DELAY = 400;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
    private long lastClickTime = 0;


    @Override
    public void onClick(View view) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            processDoubleClickEvent(view);
        }else{
            processSingleClickEvent(view);
        }
        lastClickTime = clickTime;
    }

    private void processSingleClickEvent(final View view) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onSingleClick(view);
                handler.removeCallbacks(this);
            }
        };

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);

            }
        };
        timer = new Timer();
        timer.schedule(timerTask, DELAY);
    }

    private void processDoubleClickEvent(View view) {
        if (timer!=null){
            timer.cancel();
            timer.purge();
        }
        onDoubleClick(view);

    }

    public abstract void onSingleClick(View v);

    public abstract void onDoubleClick(View v);
}

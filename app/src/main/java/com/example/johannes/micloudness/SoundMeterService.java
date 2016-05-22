package com.example.johannes.micloudness;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SoundMeterService extends Service {

    private static final String TAG = "SoundMeterService";
    private final IBinder mBinder = new SoundMeterBinder();
    private SoundMeter meter = new SoundMeter();
    private pollThread t;
    private iHandleAmpChange handler;
    private final int interval = 100;

    protected interface iHandleAmpChange {
        void handle(int val);
    }


    private class pollThread extends Thread {
        String TAG = "pollThread";
        boolean running = true;

        public void stopPoll() {
            running = false;
        }

        @Override
        public void run() {
            while(running) {
                if (meter != null) {
                    double amp = meter.getAmplitude();
                    Log.v(TAG,"measured "+amp);
                    if (handler != null) {
                        handler.handle((int)amp);
                    }
                } else {
                    Log.v(TAG,"no meter");
                }
                synchronized (this) {
                    try {
                        this.wait(interval);
                    } catch (InterruptedException e) {}
                }
            }
        }
    }

    public SoundMeterService() {
    }

    public class SoundMeterBinder extends Binder {
        protected void startMeasuring() {
            meter.start();
            if (t==null) {
                t = new pollThread();
                t.run();
            }
        }

        protected void stopMeasuring() {
            meter.stop();
            if (t!=null) {
                t.stopPoll();
            }
        }

        protected void setHandler(iHandleAmpChange handler) {
            SoundMeterService.this.handler = handler;
        }

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    //used to bind service to an activity. dont want that at the moment
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}

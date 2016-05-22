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



    private class pollThread implements Runnable {
        String TAG = "pollThread";
        boolean running = true;

        public void stop() {
            running = false;
        }

        @Override
        public void run() {
            while(running) {
                if (meter != null) {
                    double amp = meter.getAmplitude();
                    Log.v(TAG,"measured "+amp);
                } else {
                    Log.v(TAG,"no meter");
                }
                synchronized (this) {
                    try {
                        this.wait(20);
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
                t.stop();
            }
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

package com.example.johannes.micloudness;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

public class SoundMeterService extends Service {

    private static final String TAG = "SoundMeterService";
    private SoundMeter meter = new SoundMeter();
    private pollThread t;
    private final int interval = 30;

    private Looper mainServiceLooper;
    private ServiceHandler mainServiceHandler;

    //Handle Intent-Thread Interaction
    private final class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            String act = ((Intent) msg.obj).getAction();
            Log.d(TAG, "Message Action=" + act);
            if (act.equals("com.example.johannes.micloudness.MIC_START")) {
                startMeasuring();
            } else if (act.equals("com.example.johannes.micloudness.MIC_STOP")) {
                stopSelf();
            }
        }

        public ServiceHandler(Looper looper) {
            super(looper);
        }
    }

    //Thread polling the highest AMP since last read
    private class pollThread extends Thread {
        String TAG = "pollThread";
        boolean running = true;

        //stop method. sets flag to false, loop will quit
        public void stopPoll() {
            running = false;
        }

        @Override
        public void run() {
            while(running) {
                if (meter != null) {
                    double amp = meter.getAmplitude();
                    Log.v(TAG, "measured " + amp);
                    //send intent with the value to whoever wants to handle it.
                    Intent intent = new Intent();
                    intent.setAction("com.example.johannes.micloudness.MIC_VAL");
                    intent.putExtra("amp",(int)amp);
                    SoundMeterService.this.sendBroadcast(intent);
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

    @Override
    public void onCreate() {
        Log.d(TAG, "Service created");

        //handlerthread, because service would else run in main App thread. Also need the handling thingy
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                HandlerThread.NORM_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mainServiceLooper = thread.getLooper();
        mainServiceHandler = new ServiceHandler(mainServiceLooper);
    }

    //Called whenever an Intent comes in.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Message msg = mainServiceHandler.obtainMessage();
            msg.obj = intent;
            mainServiceHandler.sendMessage(msg);
        }

        //return value indicates how the system handles the destruction of the service
        //better said the recreation of it. sticky means recreation by system.
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMeasuring();
    }

    //used to bind service to an activity. dont want that at the moment
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Encapsulated Microphone Calls
    class SoundMeter {
        final String TAG = "Meter";
        private MediaRecorder mRecorder = null;

        public void start() {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.e(TAG, "ErrorMessage", e);
                }
                mRecorder.start();
            }
        }

        public void stop() {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }

        public double getAmplitude() {
            if (mRecorder != null)
                return  mRecorder.getMaxAmplitude();
            else
                return 0;

        }
    }

}

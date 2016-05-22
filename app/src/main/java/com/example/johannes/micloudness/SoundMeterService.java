package com.example.johannes.micloudness;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SoundMeterService extends Service {

    private static final String TAG = "HostService";

    private Looper mainServiceLooper;
    private ServiceHandler mainServiceHandler;

    public SoundMeterService() {
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
        return START_STICKY;

    }

    private final class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            String act = ((Intent) msg.obj).getAction();
            Log.d(TAG, "Message Action=" + act);
        }

        public ServiceHandler(Looper looper) {
            super(looper);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    //used to bind service to an activity. dont want that at the moment
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

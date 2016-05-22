package com.example.johannes.micloudness;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MicrophoneLevel extends Activity {

    private static final String TAG = "MicLevelActivity";
    private SoundMeterService.SoundMeterBinder mBoundService;
    boolean mIsBound;
    private AmpToBG color;

    private class dataHandler  implements SoundMeterService.iHandleAmpChange {

        private final int maxAmp = 32768;
        private final int min = 20;
        private final int max = 255;
        private final double changeThresh = 0.1;

        private int lastVal = 0;


        dataHandler() {}

        @Override
        public void handle(int val) {
            if (val>lastVal*(1+changeThresh)||val<lastVal*(1-changeThresh)) {
                if (color != null) {
                    int alph = (int) ((val / maxAmp) * 255);
                    if (alph < min) {
                        alph = min;
                    } else if (alph > max) {
                        alph = max;
                    }
                    //color.setColor(Color.argb(alph, 255, 255, 255));
                }
                lastVal = val;
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((SoundMeterService.SoundMeterBinder) service);
            mBoundService.setHandler(new dataHandler());
            mBoundService.startMeasuring();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this, SoundMeterService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            mBoundService.stopMeasuring();
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone_level);

        doBindService();
        color = (AmpToBG)findViewById(R.id.color);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_microphone_level, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

}

package com.example.johannes.micloudness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MicrophoneLevel extends AppCompatActivity {

    SoundMeter meter = new SoundMeter();
    PollThread t;

    class PollThread implements Runnable {
        String TAG = "pollThread";

        boolean running = true;

        public void stop() {
            running = false;
            Log.d(TAG, "stopping");
        }

        @Override
        public void run() {
            while (running) {
                if (meter!=null) {
                    double amp = meter.getAmplitude();
                    Log.v(TAG, "measured: " + amp);
                    synchronized (this) {
                        try {
                            this.wait(100);
                        } catch (InterruptedException e) {}
                    }
                } else {
                    Log.v(TAG, "no meter present");
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone_level);
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
        meter.start();
        t = new PollThread();
        t.run();
    }

    @Override
    protected void onStop() {
        super.onStop();
        t.stop();
        meter.stop();
    }
}

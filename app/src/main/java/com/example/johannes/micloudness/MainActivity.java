package com.example.johannes.micloudness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appyvet.rangebar.RangeBar;
import com.github.danielnilsson9.colorpickerview.view.ColorPanelView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;


//Generic Main Activity Class.
//Only onCreate and onDestroy have been modified to start/stop the service checking the microphone amplitude
public class MainActivity extends Activity implements ColorPickerView.OnColorChangedListener, View.OnClickListener, RangeBar.OnRangeBarChangeListener {

    private static final String TAG = "MicLevelActivity";
    private ColorPickerView mColorPickerView;
    private ColorPanelView mOldColorPanelView;
    private ColorPanelView mNewColorPanelView;
    private AmpBackgroundView mBackgroundView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        startService(new Intent(this, SoundMeterService.class).setAction("com.example.johannes.micloudness.MIC_STOP"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone_level);
        startService(new Intent(this,SoundMeterService.class).setAction("com.example.johannes.micloudness.MIC_START"));

        mColorPickerView = (ColorPickerView) findViewById(R.id.colorpickerview__color_picker_view);
        mOldColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_old);
        mNewColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_new);
        mBackgroundView = (AmpBackgroundView) findViewById(R.id.color);
        ((RangeBar) findViewById(R.id.rangebar)).setOnRangeBarChangeListener(this);

        mColorPickerView.setOnColorChangedListener(this);
        mColorPickerView.setColor(0xFFFFFF,true);
        mOldColorPanelView.setColor(0xFFFFFF);

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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onColorChanged(int newColor) {
        mNewColorPanelView.setColor(mColorPickerView.getColor());
        //newColor is the new color to use.
        mBackgroundView.setColor(newColor);
    }

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        mBackgroundView.setBounds(leftPinIndex,rightPinIndex);
    }
}

package com.example.johannes.micloudness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AmpBackgroundView extends View{

    int width = 0;
    int height = 0;
    Paint p;

    //TODO: these 2 lines
    //-----------------------------------------------
    BroadcastReceiver rec;
    final DataHandler handler = new DataHandler();
    //-----------------------------------------------

    public AmpBackgroundView(Context context) {
        super(context);
        init(null, 0);
    }
    public AmpBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public AmpBackgroundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        //initialize our paint
        p = new Paint();
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);

        width = this.getWidth();
        height = this.getHeight();

        //TODO: following part
        //-----------------------------------------------
        //Create and register receiver on the view to get intents from the SoundMeterService
        rec = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.example.johannes.micloudness.MIC_VAL")) {
                    handler.handleMicData(intent.getIntExtra("amp", 0));
                }
            }
        };
        //Filter out everything but value change intents
        IntentFilter Filter = new IntentFilter();
        Filter.addAction("com.example.johannes.micloudness.MIC_VAL");
        this.getContext().registerReceiver(rec, Filter);
        //-----------------------------------------------
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // adjust w and h so we still draw the entire canvas
        width = xNew;
        height = yNew;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        //draw the area in the current intensity
        canvas.drawRect(0, 0, width, height, p);
    }

    //TODO: use this and own implementation
    //-----------------------------------------------
    //Class to encapsulate the handling a little better
    class DataHandler {
        int maxAmp = 32768; //highest possible value from microphone
        int min = 10; //lowest value to be drawn (RGB(10,10,10))
        int max = 255; //highest possible value to be drawn (white)
        int lastVal = 0; //track last value to prevent fluttering
        //TODO: take log, not %s
        //Treshhold for change. If the value didnt change by at least 10% do nothing.
        double changeTreshhold = 0.1;

        DataHandler() {}

        public void handleMicData(int val) {
            if (val > lastVal*(1+changeTreshhold)||val < lastVal*(1+changeTreshhold)) {
                int alph = min + (int)((val*1f/maxAmp)*255);
                if (alph>max) {
                    alph = max;
                }
                //Change Color of the next draw
                p.setColor(Color.rgb(alph, alph,alph));
                invalidate();
                lastVal = val;
            }
        }
    }
    //-----------------------------------------------
}

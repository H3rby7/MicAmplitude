package com.example.johannes.micloudness;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AmpToBG extends View{

    int width = 0;
    int height = 0;
    Paint p;
    AmpReceiver rec = new AmpReceiver();
    final DataHandler handler = new DataHandler();

    public AmpToBG(Context context) {
        super(context);
        init(null, 0);
    }

    public AmpToBG(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AmpToBG(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AmpToBG, defStyle, 0);
        p = new Paint();
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        width = this.getWidth();
        height = this.getHeight();
        rec.setHandler(handler);
        IntentFilter Filter = new IntentFilter();
        Filter.addAction("com.example.johannes.micloudness.MIC_VAL");
        this.getContext().registerReceiver(rec, Filter);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        width = xNew;
        height = yNew;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int a = p.getAlpha();
        Paint.Style s = p.getStyle();
        canvas.drawRect(0, 0, width, height, p);
    }

    class DataHandler implements AmpReceiver.iMicDataHandler {
        int maxAmp = 32768;
        int min = 10;
        int max = 255;
        int lastVal = 0;
        //TODO: take log, not %s
        double changeTreshhold = 0.1;

        DataHandler() {}

        @Override
        public void handleMicData(int val) {
            if (val > lastVal*(1+changeTreshhold)||val < lastVal*(1+changeTreshhold)) {
                int alph = min + (int)((val*1f/maxAmp)*255);
                if (alph>max) {
                    alph = max;
                }
                p.setColor(Color.rgb(alph, alph,alph));
                invalidate();
                lastVal = val;
            }
        }
    }


}

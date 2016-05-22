package com.example.johannes.micloudness;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AmpToBG extends View {

    int width = 0;
    int height = 0;
    Paint p;

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
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        width = xNew;
        height = yNew;
    }

    protected void setColor(int c) {
        p.setColor(c);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        canvas.drawRect(0,0,width,height,p);
    }
}

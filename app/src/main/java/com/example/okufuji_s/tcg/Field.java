package com.example.okufuji_s.tcg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by okufuji-s on 2014/09/08.
 */
public class Field extends View {
    public Field(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        Paint p = new Paint();
        c.drawColor(Color.GRAY);
        p.setARGB(255,100,100,255);
        p.setTextSize(64);
        p.setAntiAlias(true);
        c.drawText("hello world",50,300,p);
    }
}

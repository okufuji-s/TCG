package com.example.okufuji_s.tcg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by okufuji-s on 2014/09/08.
 */
public class Field extends View {
    Paint p = new Paint();
    Bitmap back;
    Rect backrect;
    Rect mysummons,mysupport,mydeck;
    Rect enemysummons,enemysupports,enemydeck;


    public Field(Context context) {
        super(context);
        Resources res = this.getContext().getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        backrect = new Rect(0,0,back.getWidth(),back.getHeight());
        mysummons = new Rect(481,720,599,892);
        mysupport = new Rect(481,912,599,1084);
        mydeck = new Rect(800,820,918,992);
        enemysummons = new Rect(481,348,599,520);
        enemysupports = new Rect(481,156,599,328);
        enemydeck = new Rect(163,248,281,420);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        c.drawBitmap(back,backrect,mysummons,p);
        c.drawBitmap(back,backrect,mysupport,p);
        c.drawBitmap(back,backrect,mydeck,p);
        c.drawBitmap(back,backrect,enemysummons,p);
        c.drawBitmap(back,backrect,enemysupports,p);
        c.drawBitmap(back,backrect,enemydeck,p);

    }
}

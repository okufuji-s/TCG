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
    Rect mysummons,mysupport;


    public Field(Context context) {
        super(context);
        Resources res = this.getContext().getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        backrect = new Rect(0,0,back.getWidth(),back.getHeight());
        mysummons = new Rect(480,720,600,900);
        mysupport = new Rect(480,920,600,1100);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        c.drawBitmap(back,backrect,mysummons,p);
        c.drawBitmap(back,backrect,mysupport,p);
    }
}

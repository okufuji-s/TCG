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
    public Field(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        Paint p = new Paint();
        Bitmap back;
        Rect src,dst;

        Resources res = this.getContext().getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        c.drawBitmap(back,0,0,p);
        
        src = new Rect(0,0,back.getWidth(),back.getHeight());
        dst = new Rect(0,0,100,150);
        c.drawBitmap(back,src,dst,p);
    }
}

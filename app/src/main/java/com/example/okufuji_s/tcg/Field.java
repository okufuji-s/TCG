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
    Bitmap back,s0002,s1002;
    Bitmap[] card_id;
    Rect backrect,srect;
    Rect mysummons,mysupport,mydeck;
    Rect enemysummons,enemysupports,enemydeck;


    class Card {
        Bitmap bitmap;
        Rect rect;

        public Card(Context c, int bmp) {
            Resources res = c.getResources();
            bitmap = BitmapFactory.decodeResource(res,bmp);
            rect = new Rect(0,0, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    class MonsterCard extends Card {
        int HP;
        int x;
        int y;
        int z;
        public MonsterCard(Context c, int bmp, int i, int i1, int i2, int i3) {
            super(c,bmp);
            HP = i;
            x = i1;
            y = i2;
            z = i3;
        }
    }

    class SupportCard extends Card {
        int effect;
        public SupportCard(Context c, int bmp, int e){
            super(c, bmp);
            effect = e;
        }
    }
    protected Card[] card = new Card[3];

    public Field(Context context) {
        super(context);

        Resources res = context.getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        backrect = new Rect(0,0,back.getWidth(),back.getHeight());

        mysummons = new Rect(481,720,599,892);
        mysupport = new Rect(481,912,599,1084);
        mydeck = new Rect(800,820,918,992);
        enemysummons = new Rect(481,348,599,520);
        enemysupports = new Rect(481,156,599,328);
        enemydeck = new Rect(163,248,281,420);

        card[0] = new MonsterCard(context, R.drawable.s0002, 100,100,100,100);
        card[1] = new SupportCard(context, R.drawable.s1002, 0);
        card[2] = new SupportCard(context, R.drawable.s1002, 0);
        /*
        card_id = new Bitmap[3];
        card_id[0]=back;
        card_id[1]=s0002;
        card_id[2]=s1002;
        */
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        c.drawBitmap(card[0].bitmap,card[0].rect,mysummons,p);
        c.drawBitmap(card[1].bitmap,card[1].rect,mysupport,p);
        c.drawBitmap(card[2].bitmap,card[2].rect,mydeck,p);

        /*
        c.drawBitmap(s0002,srect,mysummons,p);
        c.drawBitmap(back,backrect,mysupport,p);
        c.drawBitmap(back,backrect,mydeck,p);
        c.drawBitmap(back,backrect,enemysummons,p);
        c.drawBitmap(back,backrect,enemysupports,p);
        c.drawBitmap(back,backrect,enemydeck,p);
        */

    }
}

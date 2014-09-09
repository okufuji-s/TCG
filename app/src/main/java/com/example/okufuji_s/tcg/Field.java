package com.example.okufuji_s.tcg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Collections;
import java.util.Random;
import java.util.Vector;

/**
 * Created by okufuji-s on 2014/09/08.
 */
public class Field extends View {
    Paint p = new Paint();
    Bitmap back,width;
    Rect backrect;
    Rect mysummons,mysupport,mydeck;
    Rect enemysummons,enemysupports,enemydeck;
    //Vector<Card> mydecks,enemydecks;
    int[] decka,deckb;

    Vector<Card> mydecks = new Vector<Card>();
    Vector<Card> enemydecks = new Vector<Card>();
    Vector<Card> myhands = new Vector<Card>();
    Vector<Card> enemyhands = new Vector<Card>();

    int displaywidth;

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
    protected Card[] card = new Card[24];

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

        card[0] = new MonsterCard(context, R.drawable.s0001, 100,100,100,100);
        card[1] = new MonsterCard(context, R.drawable.s0002, 100,100,100,100);
        card[2] = new MonsterCard(context, R.drawable.s0003, 100,100,100,100);
        card[3] = new MonsterCard(context, R.drawable.s0004, 100,100,100,100);
        card[4] = new MonsterCard(context, R.drawable.s0005, 100,100,100,100);
        card[5] = new MonsterCard(context, R.drawable.s0006, 100,100,100,100);
        card[6] = new MonsterCard(context, R.drawable.s0007, 100,100,100,100);
        card[7] = new MonsterCard(context, R.drawable.s0008, 100,100,100,100);
        card[8] = new MonsterCard(context, R.drawable.s0009, 100,100,100,100);
        card[9] = new MonsterCard(context, R.drawable.s0010, 100,100,100,100);
        card[10] = new MonsterCard(context, R.drawable.s0011, 100,100,100,100);
        card[11] = new MonsterCard(context, R.drawable.s0012, 100,100,100,100);
        card[12] = new MonsterCard(context, R.drawable.s0013, 100,100,100,100);
        card[13] = new MonsterCard(context, R.drawable.s0014, 100,100,100,100);
        card[14] = new MonsterCard(context, R.drawable.s0015, 100,100,100,100);
        card[15] = new MonsterCard(context, R.drawable.s0016, 100,100,100,100);
        card[16] = new SupportCard(context, R.drawable.s1001, 0);
        card[17] = new SupportCard(context, R.drawable.s1002, 0);
        card[18] = new SupportCard(context, R.drawable.s1003, 0);
        card[19] = new SupportCard(context, R.drawable.s1004, 0);
        card[20] = new SupportCard(context, R.drawable.s1005, 0);
        card[21] = new SupportCard(context, R.drawable.s1006, 0);
        card[22] = new SupportCard(context, R.drawable.s1007, 0);
        card[23] = new SupportCard(context, R.drawable.s1008, 0);

        int[] decka = {0,1,2,3,8,9,10,11,16,20};
        int[] deckb = {4,5,6,7,12,13,14,15,16,20};

        for(int k = 0;k<10;k++){
            for(int i = 0; i<4;i++) {
                mydecks.addElement(card[decka[k]]);
                enemydecks.addElement(card[deckb[k]]);
            }
        }
        Collections.shuffle(mydecks);
        Collections.shuffle(enemydecks);
        for(int i=0;i<5;i++) {
            myhands.addElement(mydecks.remove(0));
        }

            // リソースからbitmapを作成
            width = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            // WindowManager取得
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            // Displayインスタンス生成
            Display dp = wm.getDefaultDisplay();
            displaywidth = dp.getWidth();
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        Card test;
        test = mydecks.get(0);

        c.drawBitmap(card[0].bitmap,card[0].rect,mysummons,p);
        //c.drawBitmap(card[1].bitmap,card[1].rect,mysupport,p);
        //c.drawBitmap(card[2].bitmap,card[2].rect,mydeck,p);
        c.drawBitmap(test.bitmap,test.rect,mydeck,p);
        /*
        p.setARGB(255,100,100,255);
        p.setTextSize(100);
        c.drawText(String.valueOf(displaywidth),100,100,p);
        */

        Rect[] myhandsrect = new Rect[myhands.size()];
        for(int i=0; i<myhands.size(); i++){
            myhandsrect[i] = new Rect(displaywidth/myhands.size()*i,1200,displaywidth/myhands.size()*i+118,1392);
            test=myhands.get(i);
            c.drawBitmap(test.bitmap,test.rect,myhandsrect[i],p);
        }
    }
}

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
import android.view.MotionEvent;
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
    Paint button = new Paint();
    Bitmap back, width;
    Rect backrect;
    Rect mysummons, mysupport, mydeck;
    Rect enemysummons, enemysupports, enemydeck;
    //Vector<Card> mydecks,enemydecks;
    int[] decka, deckb;

    Vector<Card> mydecks = new Vector<Card>();
    Vector<Card> enemydecks = new Vector<Card>();
    Vector<Card> myhands = new Vector<Card>();
    Vector<Card> enemyhands = new Vector<Card>();
    Vector<Card> mytrash = new Vector<Card>();
    Vector<Card> enemytrash = new Vector<Card>();

    int displaywidth;
    int touchx, touchy;  //触った場所の座標

    int turn_count = 0;

    enum Game_state {
        start,
        setfirst,
        mydraw,
        mybattle,
    }
    Game_state state = Game_state.start;

    class Card {
        Bitmap bitmap;
        Rect rect;

        public Card(Context c, int bmp) {
            Resources res = c.getResources();
            bitmap = BitmapFactory.decodeResource(res, bmp);
            rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    class MonsterCard extends Card {
        int HP;
        int x;
        int y;
        int z;
        int rank;
        String summonscolor;   //red=0,blue=1,green=2,yellow=3

        public MonsterCard(Context c, int bmp, int i, int i1, int i2, int i3, int i4, String s) {
            super(c, bmp);
            HP = i;
            x = i1;
            y = i2;
            z = i3;
            rank = i4;
            summonscolor = s;
        }
    }

    class SupportCard extends Card {
        int effect;

        public SupportCard(Context c, int bmp, int e) {
            super(c, bmp);
            effect = e;
        }
    }

    protected Card[] card = new Card[24];
    MonsterCard myplaysummons,enemyplaysummons; //場に出ているものそれ自体
    int my_rank=0,my_HP,enemy_rank=0,enemy_HP;
    String my_color,enemy_color;

    public Field(Context context) {
        super(context);

        Resources res = context.getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        backrect = new Rect(0, 0, back.getWidth(), back.getHeight());

        mysummons = new Rect(481, 720, 599, 892);
        mysupport = new Rect(481, 912, 599, 1084);
        mydeck = new Rect(800, 820, 918, 992);
        enemysummons = new Rect(481, 348, 599, 520);
        enemysupports = new Rect(481, 156, 599, 328);
        enemydeck = new Rect(163, 248, 281, 420);

        card[0] = new MonsterCard(context, R.drawable.s0001, 480, 100, 100, 100, 0, "Red");
        card[1] = new MonsterCard(context, R.drawable.s0002, 650, 100, 100, 100, 0, "Blue");
        card[2] = new MonsterCard(context, R.drawable.s0003, 500, 100, 100, 100, 0, "Green");
        card[3] = new MonsterCard(context, R.drawable.s0004, 560, 100, 100, 100, 0, "Yellow");
        card[4] = new MonsterCard(context, R.drawable.s0005, 100, 100, 100, 100, 1, "Red");
        card[5] = new MonsterCard(context, R.drawable.s0006, 100, 100, 100, 100, 1, "Blue");
        card[6] = new MonsterCard(context, R.drawable.s0007, 100, 100, 100, 100, 1, "Green");
        card[7] = new MonsterCard(context, R.drawable.s0008, 100, 100, 100, 100, 1, "Yellow");
        card[8] = new MonsterCard(context, R.drawable.s0009, 100, 100, 100, 100, 2, "Red");
        card[9] = new MonsterCard(context, R.drawable.s0010, 100, 100, 100, 100, 2, "Blue");
        card[10] = new MonsterCard(context, R.drawable.s0011, 100, 100, 100, 100, 2, "Green");
        card[11] = new MonsterCard(context, R.drawable.s0012, 100, 100, 100, 100, 2, "Yellow");
        card[12] = new MonsterCard(context, R.drawable.s0013, 100, 100, 100, 100, 3, "Red");
        card[13] = new MonsterCard(context, R.drawable.s0014, 100, 100, 100, 100, 3, "Blue");
        card[14] = new MonsterCard(context, R.drawable.s0015, 100, 100, 100, 100, 3, "Green");
        card[15] = new MonsterCard(context, R.drawable.s0016, 100, 100, 100, 100, 3, "Yellow");
        card[16] = new SupportCard(context, R.drawable.s1001, 0);
        card[17] = new SupportCard(context, R.drawable.s1002, 0);
        card[18] = new SupportCard(context, R.drawable.s1003, 0);
        card[19] = new SupportCard(context, R.drawable.s1004, 0);
        card[20] = new SupportCard(context, R.drawable.s1005, 0);
        card[21] = new SupportCard(context, R.drawable.s1006, 0);
        card[22] = new SupportCard(context, R.drawable.s1007, 0);
        card[23] = new SupportCard(context, R.drawable.s1008, 0);

        int[] decka = {0, 1, 14, 15, 18, 19, 12, 13, 16, 20};
        int[] deckb = {2, 3, 10, 10, 10, 11, 11, 10, 11, 10};
        for (int k = 0; k < 10; k++) {
            for (int i = 0; i < 4; i++) {
                mydecks.addElement(card[decka[k]]);
                enemydecks.addElement(card[deckb[k]]);
            }
        }                                                   //デッキ作成箇所

        Collections.shuffle(mydecks);
        Collections.shuffle(enemydecks);
        mymulligan();
        enemymulligan();
        boolean myexchange=false,enemyexchange=false;
        while(myexchange==false){
            Card checkcard;
            for(int i = 0; i<myhands.size(); i++){
                checkcard = myhands.get(i);
                Class cls = checkcard.getClass();
                if(cls == MonsterCard.class){
                    MonsterCard m = (MonsterCard) checkcard;
                    if(m.rank==0){
                        myexchange = true;
                    }
                }
            }
            if(myexchange == false){
                mymulligan();
            }
        }
        while(enemyexchange==false){
            Card checkcard;
            for(int i = 0; i<enemyhands.size(); i++){
                checkcard = enemyhands.get(i);
                Class cls = checkcard.getClass();
                if(cls == MonsterCard.class){
                    MonsterCard m = (MonsterCard) checkcard;
                    if(m.rank==0){
                        enemyexchange = true;
                    }
                }
            }
            if(enemyexchange == false){
                enemymulligan();
            }
        }


        // リソースからbitmapを作成
        width = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        // WindowManager取得
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // Displayインスタンス生成
        Display dp = wm.getDefaultDisplay();
        displaywidth = dp.getWidth();

        state = Game_state.setfirst;
    }

    void enemymulligan(){
        enemytrash.addAll(enemyhands);
        enemyhands.clear();
        for (int i = 0; i < 5; i++) {
            enemyhands.addElement(enemydecks.remove(0));
        }
    }
    void mymulligan(){
        mytrash.addAll(myhands);
        myhands.clear();
        for (int i = 0; i < 5; i++) {
            myhands.addElement(mydecks.remove(0));
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        Card test;

        c.drawBitmap(back, backrect, mydeck, p);
        c.drawBitmap(back, backrect, enemydeck, p);
        p.setARGB(255,0,0,0);
        p.setTextSize(50);
        p.setAntiAlias(true);
        button.setARGB(255,0,0,0);
        button.setTextSize(250);
        c.drawText(String.valueOf(mydecks.size()),810,870,p);
        c.drawText(String.valueOf(enemydecks.size()),173,298,p);
        c.drawText("trash:" + String.valueOf(mytrash.size()),810,1100,p);
        c.drawText("trash:" + String.valueOf(enemytrash.size()),173,208,p);
        c.drawText("HP:" + String.valueOf(my_HP),173,730,p);
        c.drawText("rank:" + String.valueOf(my_rank),173,810,p);
        c.drawText("color:" + my_color,173,870,p);
        c.drawText("HP:" + String.valueOf(enemy_HP),630,308,p);
        c.drawText("rank:" + String.valueOf(enemy_rank),630,388,p);
        c.drawText("color:" + enemy_color,630,448,p);

        if(state == Game_state.mydraw){
            c.drawText("あなたのターンです。タップでドロー",100,595,p);
        }
        if(state == Game_state.mybattle){
            c.drawText("[先行]戦闘です。↓のボタンをタップ！",100,595,p);
            c.drawText(" x    y    z ",0,1600,button);
        }

        if(myplaysummons != null){
            c.drawBitmap(myplaysummons.bitmap,myplaysummons.rect,mysummons,p);
        }
        if(enemyplaysummons != null){
            c.drawBitmap(enemyplaysummons.bitmap,enemyplaysummons.rect,enemysummons,p);
        }

        Rect[] myhandsrect = new Rect[myhands.size()];
        for (int i = 0; i < myhands.size(); i++) {
            myhandsrect[i] = new Rect(displaywidth / myhands.size() * i, 1200, displaywidth / myhands.size() * i + 118, 1392);
            test = myhands.get(i);
            c.drawBitmap(test.bitmap, test.rect, myhandsrect[i], p);
        }
        Rect[] enemyhandsrect = new Rect[enemyhands.size()];
        for (int i = 0; i < enemyhands.size(); i++) {
            enemyhandsrect[i] = new Rect(displaywidth / enemyhands.size() * i, -120, displaywidth / enemyhands.size() * i + 118, 72);
            test = enemyhands.get(i);
            c.drawBitmap(back, backrect, enemyhandsrect[i], p);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            touchx = (int) ev.getX();
            touchy = (int) ev.getY();
            if(state == Game_state.mydraw){
                mydraw();
            }
            if(state == Game_state.setfirst){
                //最初にランク0を出そうとするところ
                firstsummons();
                state = Game_state.mydraw;
            }
            Field.this.invalidate();
        }
        return true;
    }

    void firstsummons(){
        Card check,putsummon;
        for (int i = 0; i < myhands.size(); i++) {
            if (displaywidth / myhands.size() * i < touchx && touchx < displaywidth / myhands.size() * i + 118 && 1200 < touchy && touchy < 1392) {
                check = myhands.get(i);
                Class cls = check.getClass();
                if (cls == MonsterCard.class) {
                    MonsterCard m;
                    m = (MonsterCard) check;

                    if (m.rank == 0) {   /*0なら*/
                        Vector<Card> s = new Vector<Card>();
                        putsummon = myhands.remove(i);
                        myplaysummons = (MonsterCard)putsummon;
                        my_HP = myplaysummons.HP;
                        my_color = myplaysummons.summonscolor;
                        //相手のランク0を出すところ
                        for(int k=0; k<enemyhands.size(); k++){
                            check = enemyhands.get(k);
                            cls = check.getClass();
                            if(cls == MonsterCard.class){
                                MonsterCard n = (MonsterCard) check;
                                if (n.rank==0){
                                    s.addElement(enemyhands.remove(k));
                                }
                            }

                        }
                        Random random = new Random();
                        int ran = random.nextInt(s.size());
                        putsummon = s.remove(ran);
                        enemyplaysummons = (MonsterCard)putsummon;
                        enemyhands.addAll(s);
                        enemy_HP = enemyplaysummons.HP;
                        enemy_color = enemyplaysummons.summonscolor;
                    }
                }
            }
        }
    }

    void mydraw(){
        myhands.addElement(mydecks.remove(0));
        if(turn_count != 0) myhands.addElement(mydecks.remove(0)); //とりあえず最初以外は2ドロー
        state = Game_state.mybattle;
    }
}

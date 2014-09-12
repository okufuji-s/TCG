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
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import static android.view.GestureDetector.OnGestureListener;

/**
 * Created by okufuji-s on 2014/09/08.
 */
public class Field extends View implements OnGestureListener {
    Paint p = new Paint();
    Paint button = new Paint();
    Bitmap back, width, itai;
    Rect backrect, itairect, kakudai;
    Rect mysummons, mysupport, mydeck;
    Rect enemysummons, enemysupports, enemydeck;
    //Vector<Card> mydecks,enemydecks;
    boolean myitasou = false, enemyitasou = false;
    boolean longtap = false;
    boolean mybutton = false,enemybutton = false;

    Vector<Card> mydecks = new Vector<Card>();
    Vector<Card> enemydecks = new Vector<Card>();
    Vector<Card> myhands = new Vector<Card>();
    Vector<Card> enemyhands = new Vector<Card>();
    Vector<Card> mytrash = new Vector<Card>();
    Vector<Card> enemytrash = new Vector<Card>();
    Vector<Card> trm1 = new Vector<Card>();
    Vector<Card> trm2 = new Vector<Card>();

    int displaywidth;
    int touchx, touchy;  //触った場所の座標

    int turn_count = 0;

    enum Game_state {
        start,
        setfirst,   //最初の
        mydraw,
        battle,
        waitbuttle,
        enemydraw,
        myattack,
        enemyattack,
        mynewsummons,       //自分のしんだら
        enemynewsummons,    //相手のしんだら
        myef,
        enemyef,
        win,
        enemywin,
        setsupport,
        supporteffect,
        myeffect,
        enemyeffect,
        noeffect,
    }

    Game_state state = Game_state.mydraw;
    Game_state attackstate = Game_state.myattack;
    Game_state effectstate = Game_state.noeffect;

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
        int ef;

        public MonsterCard(Context c, int bmp, int i, int i1, int i2, int i3, int i4, String s ,int ze) {
            super(c, bmp);
            HP = i;
            x = i1;
            y = i2;
            z = i3;
            rank = i4;
            summonscolor = s;
            ef = ze;

        }
    }

    class SupportCard extends Card {
        int effect;

        public SupportCard(Context c, int bmp, int e) {
            super(c, bmp);
            effect = e;
        }
    }

    protected Card[] card = new Card[27];
    Card kakudaicard;
    MonsterCard myplaysummons, enemyplaysummons; //場に出ているものそれ自体
    SupportCard myplaysupport, enemyplaysupport;
    int my_rank = 0, my_HP, enemy_rank = 0, enemy_HP, my_x, my_y, my_z, enemy_x, enemy_y, enemy_z;
    String my_color, enemy_color;
    String myselectbutton, enemyselectbutton;
    boolean mysummonsdead = false, enemysummonsdead = false;
    Timer timer;
    Handler handler = new Handler();

    class Timeract extends TimerTask {
        int a;

        public Timeract(int i) {
            a = i;
        }

        @Override
        public void run() {
            if (a == 1) {
                myattack();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
            if (a == 2) {
                enemyattack();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
            if (a == 3) {
                mysetsupport();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
            if (a == 4) {
                enemysetsupport();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
            if (a == 5) {
                effectstate = Game_state.myeffect;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
                mysupporteffect();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
            if (a == 6) {
                effectstate = Game_state.enemyeffect;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
                enemysupporteffect();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
            if (a == 7) {
                effectstate = Game_state.myef;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
                myef();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
            if (a == 8) {
                effectstate = Game_state.enemyef;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
                enemyef();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Field.this.invalidate();
                    }
                });
            }
        }

    }

    GestureDetector gestureDetector;

    public Field(Context context) {
        super(context);

        this.gestureDetector = new GestureDetector(Field.this.getContext(), this);
        Resources res = context.getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        backrect = new Rect(0, 0, back.getWidth(), back.getHeight());
        itai = BitmapFactory.decodeResource(res, R.drawable.itai);
        itairect = new Rect(0, 0, itai.getWidth(), itai.getHeight());

        mysummons = new Rect(481, 720, 599, 892);
        mysupport = new Rect(481, 912, 599, 1084);
        mydeck = new Rect(800, 820, 918, 992);
        enemysummons = new Rect(481, 348, 599, 520);
        enemysupports = new Rect(481, 156, 599, 328);
        enemydeck = new Rect(163, 248, 281, 420);
        kakudai = new Rect(0, 110, 354, 626);

        card[0] = new MonsterCard(context, R.drawable.s0001, 480, 320, 240, 120, 0, "Red", 0);
        card[1] = new MonsterCard(context, R.drawable.s0002, 650, 240, 180, 160, 0, "Blue",0);
        card[2] = new MonsterCard(context, R.drawable.s0003, 500, 300, 250, 150, 0, "Green",0);
        card[3] = new MonsterCard(context, R.drawable.s0004, 560, 270, 200, 170, 0, "Yellow",2);
        card[4] = new MonsterCard(context, R.drawable.s0005, 670, 560, 410, 260, 1, "Red",3);
        card[5] = new MonsterCard(context, R.drawable.s0006, 870, 480, 320, 230, 1, "Blue",4);
        card[6] = new MonsterCard(context, R.drawable.s0007, 810, 420, 350, 330, 1, "Green",5);
        card[7] = new MonsterCard(context, R.drawable.s0008, 780, 450, 360, 270, 1, "Yellow",6); //3 = 対黄　4 = 対赤　5 = 対青　6 = 対緑
        card[8] = new MonsterCard(context, R.drawable.s0009, 950, 700, 480, 370, 2, "Red",0);
        card[9] = new MonsterCard(context, R.drawable.s0010, 1190, 560, 310, 190, 2, "Blue",7);
        card[10] = new MonsterCard(context, R.drawable.s0011, 990, 630, 340, 190, 2, "Green",0);
        card[11] = new MonsterCard(context, R.drawable.s0012, 1090, 630, 340, 190, 2, "Yellow",8);
        card[12] = new MonsterCard(context, R.drawable.s0013, 1690, 810, 520, 380, 3, "Red",1);
        card[13] = new MonsterCard(context, R.drawable.s0014, 1960, 590, 420, 370, 3, "Blue",0);
        card[14] = new MonsterCard(context, R.drawable.s0015, 1800, 700, 500, 400, 3, "Green",2);
        card[15] = new MonsterCard(context, R.drawable.s0016, 1470, 660, 470, 400, 3, "Yellow",9);
        card[16] = new SupportCard(context, R.drawable.s1001, 1);
        card[17] = new SupportCard(context, R.drawable.s1002, 2);
        card[18] = new SupportCard(context, R.drawable.s1003, 3);
        card[19] = new SupportCard(context, R.drawable.s1004, 4);
        card[20] = new SupportCard(context, R.drawable.s1005, 0);
        card[21] = new SupportCard(context, R.drawable.s1006, 0);
        card[22] = new SupportCard(context, R.drawable.s1007, 0);
        card[23] = new SupportCard(context, R.drawable.s1008, 8);
        card[24] = new SupportCard(context, R.drawable.s1046, 9);
        card[25] = new SupportCard(context, R.drawable.s1047, 10);
        card[26] = new SupportCard(context, R.drawable.s1048, 11);

        int[] decka = {0, 4, 7, 8, 11, 12, 18, 17, 19, 23};
        int[] deckb = {2, 6, 5, 9, 10, 15, 14, 24, 25, 26};
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
        boolean myexchange = false, enemyexchange = false;
        while (myexchange == false) {
            Card checkcard;
            for (int i = 0; i < myhands.size(); i++) {
                checkcard = myhands.get(i);
                Class cls = checkcard.getClass();
                if (cls == MonsterCard.class) {
                    MonsterCard m = (MonsterCard) checkcard;
                    if (m.rank == 0) {
                        myexchange = true;
                    }
                }
            }
            if (myexchange == false) {
                mymulligan();
            }
        }
        while (enemyexchange == false) {
            Card checkcard;
            for (int i = 0; i < enemyhands.size(); i++) {
                checkcard = enemyhands.get(i);
                Class cls = checkcard.getClass();
                if (cls == MonsterCard.class) {
                    MonsterCard m = (MonsterCard) checkcard;
                    if (m.rank == 0) {
                        enemyexchange = true;
                    }
                }
            }
            if (enemyexchange == false) {
                enemymulligan();
            }
        }
        timer = new Timer();

        // リソースからbitmapを作成
        width = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        // WindowManager取得
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // Displayインスタンス生成
        Display dp = wm.getDefaultDisplay();
        displaywidth = dp.getWidth();
        state = Game_state.setfirst;
    }

    void enemymulligan() {
        enemytrash.addAll(enemyhands);
        enemyhands.clear();
        for (int i = 0; i < 5; i++) {
            enemyhands.addElement(enemydecks.remove(0));
        }
    }

    void mymulligan() {
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
        p.setARGB(255, 0, 0, 0);
        p.setTextSize(50);
        p.setAntiAlias(true);
        button.setARGB(255, 0, 0, 0);
        button.setTextSize(250);
        c.drawText(String.valueOf(mydecks.size()), 810, 870, p);
        c.drawText(String.valueOf(enemydecks.size()), 173, 298, p);
        c.drawText("trash:" + String.valueOf(mytrash.size()), 810, 1100, p);
        c.drawText("trash:" + String.valueOf(enemytrash.size()), 173, 208, p);
        c.drawText("HP:" + String.valueOf(my_HP), 173, 730, p);
        c.drawText("rank:" + String.valueOf(my_rank), 173, 810, p);
        c.drawText("color:" + my_color, 173, 870, p);
        c.drawText("HP:" + String.valueOf(enemy_HP), 630, 308, p);
        c.drawText("rank:" + String.valueOf(enemy_rank), 630, 388, p);
        c.drawText("color:" + enemy_color, 630, 448, p);


        if (kakudaicard != null) c.drawBitmap(kakudaicard.bitmap, kakudaicard.rect, kakudai, p);


        if (state == Game_state.setfirst) c.drawText("ランク0の最初の召喚獣を選んでください", 100, 595, p);
        if (state == Game_state.mynewsummons) c.drawText("[瀕死しています]召喚獣をプレイしてください", 0, 595, p);
        if (state == Game_state.mynewsummons) c.drawText("蘇生の画面の上のほうをタップしてください。",0,1695,p);
        if (state == Game_state.enemynewsummons) c.drawText("相手は新しい召喚獣をプレイします。", 150, 595, p);
        if (state == Game_state.mydraw) c.drawText("あなたのターンです。タップでドロー", 100, 595, p);
        if (state == Game_state.setsupport) c.drawText("補助カードを選択してください。", 150, 595, p);
        if (state == Game_state.setsupport) c.drawText("使わない場合は手札の下をタップしてください。",0,1695,p);
        if (state == Game_state.battle && attackstate == Game_state.myattack) {
            c.drawText("[先行]戦闘です。↓のボタンをタップ！", 100, 595, p);
            c.drawText(" x    y    z ", 0, 1600, button);
        }
        if (state == Game_state.enemydraw) c.drawText("あいてのターンをタップで始めます。", 100, 595, p);
        if (state == Game_state.battle && attackstate == Game_state.enemyattack) {
            c.drawText("[後攻]戦闘です。↓のボタンをタップ！", 100, 595, p);
            c.drawText(" x    y    z ", 0, 1600, button);
        }
        if (state == Game_state.win) c.drawText("[あなたの勝利]おめでとうございます", 100, 595, p);
        if (state == Game_state.enemywin) c.drawText("[相手の勝利]残念でした・・・", 200, 595, p);
        if (effectstate == Game_state.myeffect && myplaysupport != null) {
            switch (myplaysupport.effect) {
                case 0:
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    c.drawText("攻撃力を上げます。", 400, 595, p);
                    break;
                case 8:
                    c.drawText("300回復します。", 400, 595, p);
                    break;
                case 9:
                case 10:
                case 11:
                    c.drawText("攻撃力が0になります。",300,595,p);
                    break;
            }
        }
        if (effectstate == Game_state.enemyeffect && enemyplaysupport != null) {
            switch (enemyplaysupport.effect) {
                case 0:
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    c.drawText("攻撃力を上げます。", 400, 595, p);
                    break;
                case 8:
                    c.drawText("300回復します。", 400, 595, p);
                    break;
                case 9:
                case 10:
                case 11:
                    c.drawText("攻撃力が0になります。",300,595,p);
            }
        }

        if (state == Game_state.setsupport && myplaysupport != null)
            c.drawBitmap(back, backrect, mysupport, p);
        if (state == Game_state.setsupport && enemyplaysupport != null)
            c.drawBitmap(back, backrect, enemysupports, p);
        if (state == Game_state.battle && myplaysupport != null)
            c.drawBitmap(back, backrect, mysupport, p);
        if (state == Game_state.battle && enemyplaysupport != null)
            c.drawBitmap(back, backrect, enemysupports, p);
        if (state == Game_state.supporteffect && myplaysupport != null)
            c.drawBitmap(myplaysupport.bitmap, myplaysupport.rect, mysupport, p);
        if (state == Game_state.supporteffect && enemyplaysupport != null)
            c.drawBitmap(enemyplaysupport.bitmap, enemyplaysupport.rect, enemysupports, p);
        if (myplaysummons != null)
            c.drawBitmap(myplaysummons.bitmap, myplaysummons.rect, mysummons, p);
        if (enemyplaysummons != null)
            c.drawBitmap(enemyplaysummons.bitmap, enemyplaysummons.rect, enemysummons, p);
        if(mybutton == true){
            c.drawText(myselectbutton,475,890,button);
            mybutton = false;
        }
        if(enemybutton == true){
            c.drawText(enemyselectbutton,475,520,button);
            enemybutton = false;
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
        if (myitasou == true) c.drawBitmap(itai, itairect, mysummons, p);
        if (enemyitasou == true) c.drawBitmap(itai, itairect, enemysummons, p);


    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        touchx = (int) ev.getX();
        touchy = (int) ev.getY();
        gestureDetector.onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            longtap = false;
            Log.d("test", String.valueOf(touchy));
            Field.this.invalidate();
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (longtap == false) {
                if (state == Game_state.battle) {
                    selectbutton();
                    Field.this.invalidate();
                    if (attackstate == Game_state.myattack) wait(6, 1000);
                    if (attackstate == Game_state.enemyattack) wait(5, 1000);
                    turn_count++;
                }
                if (state == Game_state.setsupport) {
                    if (attackstate == Game_state.myattack) wait(4, 1000);
                    if (attackstate == Game_state.enemyattack) wait(3, 1000);
                }
                if (state == Game_state.enemydraw) {
                    enemydraw();
                }
                if (state == Game_state.mydraw) {
                    mydraw();
                }
                if (state == Game_state.setfirst) {
                    //最初にランク0を出そうとするところ
                    firstsummons();
                }
                if (state == Game_state.mynewsummons) {
                    mynewsummmons();
                }
                if (state == Game_state.enemynewsummons) {
                    enemynewsummons();
                }
                Field.this.invalidate();
            }
            if (longtap == true) {
                kakudaicard = null;
                Field.this.invalidate();
                longtap = false;
            }
        }
        return true;
    }


    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("log", "フリックしてます");
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("log", "Scrollしてますよ");
        return false;
    }

    public void onShowPress(MotionEvent e) {
        Log.d("log", "Showpressしてますよ");
    }

    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("log", "シングルタップしてますよ");
        return false;
    }

    public boolean onDown(MotionEvent e) {
        Log.d("log", "押してますよ");
        return false;
    }


    public void onLongPress(MotionEvent e) {
        Log.d("log", "長押し成功してますよ");
        longtap = true;
        for (int i = 0; i < myhands.size(); i++) {
            if (displaywidth / myhands.size() * i < touchx && touchx < displaywidth / myhands.size() * i + 118 && 1200 < touchy && touchy < 1392) {
                kakudaicard = myhands.get(i);
            }
        }
        if(481 < touchx && touchx < 599 && 720 < touchy && touchy < 892) kakudaicard = myplaysummons;
        if(481 < touchx && touchx < 599 && 348 < touchy && touchy < 520) kakudaicard = enemyplaysummons;
        Field.this.invalidate();
    }


    void firstsummons() {
        Card check, putsummon;
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
                        myplaysummons = (MonsterCard) putsummon;
                        my_HP = myplaysummons.HP;
                        my_x = myplaysummons.x;
                        my_y = myplaysummons.y;
                        my_z = myplaysummons.z;
                        my_color = myplaysummons.summonscolor;
                        //相手のランク0を出すところ
                        for (int k = 0; k < enemyhands.size(); k++) {
                            check = enemyhands.get(k);
                            cls = check.getClass();
                            if (cls == MonsterCard.class) {
                                MonsterCard n = (MonsterCard) check;
                                if (n.rank == 0) {
                                    s.addElement(enemyhands.remove(k));
                                }
                            }

                        }
                        Random random = new Random();
                        int ran = random.nextInt(s.size());
                        putsummon = s.remove(ran);
                        enemyplaysummons = (MonsterCard) putsummon;
                        enemyhands.addAll(s);
                        enemy_HP = enemyplaysummons.HP;
                        enemy_x = enemyplaysummons.x;
                        enemy_y = enemyplaysummons.y;
                        enemy_z = enemyplaysummons.z;
                        enemy_color = enemyplaysummons.summonscolor;
                        state = Game_state.mydraw;
                    }
                }
            }
        }
    }

    void mydraw() {
        originattackpoint();
        attackstate = Game_state.myattack;
        effectstate = Game_state.noeffect;
        myitasou = false;
        enemyitasou = false;
        if (mydecks.size() != 0) myhands.addElement(mydecks.remove(0));
        if (turn_count != 0 && mydecks.size() != 0) myhands.addElement(mydecks.remove(0)); //とりあえず最初以外は2ドロー
        if (mysummonsdead == true) state = Game_state.mynewsummons;
        if (mysummonsdead == false) state = Game_state.setsupport;
    }

    void enemydraw() {
        originattackpoint();
        attackstate = Game_state.enemyattack;
        effectstate = Game_state.noeffect;
        myitasou = false;
        enemyitasou = false;
        if (enemydecks.size() != 0) enemyhands.addElement(enemydecks.remove(0));
        if (turn_count != 0 && enemydecks.size() != 0)
            enemyhands.addElement(enemydecks.remove(0)); //とりあえず最初以外は2ドロー
        if (enemysummonsdead == true) state = Game_state.enemynewsummons;
        if (enemysummonsdead == false) state = Game_state.setsupport;
    }

    void selectbutton() {
        if (touchx < 220 && 1400 < touchy) myselectbutton = "x";
        if (450 < touchx && touchx < 600 && 1400 < touchy) myselectbutton = "y";
        if (800 < touchx && 1400 < touchy) myselectbutton = "z";
        Random random = new Random();
        int i = random.nextInt(3);
        if (i == 0) enemyselectbutton = "x";
        if (i == 1) enemyselectbutton = "y";
        if (i == 2) enemyselectbutton = "z";
        state = Game_state.supporteffect;
    }

    void myattack() {
        myitasou = false;
        if (mysummonsdead == false) {
            if (myselectbutton.equals("x")) {
                enemy_HP -= my_x;
                enemyitasou = true;
            }
            if (myselectbutton.equals("y")) {
                enemy_HP -= my_y;
                enemyitasou = true;
            }
            if (myselectbutton.equals("z")) {
                enemy_HP -= my_z;
                enemyitasou = true;
            }
        }
        if (enemy_HP <= 0) enemysummonsdead = true;
        if (enemy_HP <= 0 && enemy_rank >= 3) state = Game_state.win;
        if (state != Game_state.win && attackstate == Game_state.myattack) wait(2, 1000);
        if (state != Game_state.win && attackstate == Game_state.enemyattack)
            state = Game_state.mydraw;
        myplaysupport = null;
        mytrash.addAll(trm1);
        trm1.clear();
    }

    void enemyattack() {
        enemyitasou = false;
        if (enemysummonsdead == false) {
            if (enemyselectbutton.equals("x")) {
                my_HP -= enemy_x;
                myitasou = true;
            }
            if (enemyselectbutton.equals("y")) {
                my_HP -= enemy_y;
                myitasou = true;
            }
            if (enemyselectbutton.equals("z")) {
                my_HP -= enemy_z;
                myitasou = true;
            }
        }
        if (my_HP <= 0) mysummonsdead = true;
        if (my_HP <= 0 && my_rank >= 3) state = Game_state.enemywin;
        if (state != Game_state.enemywin && attackstate == Game_state.enemyattack) wait(1, 1000);
        if (state != Game_state.enemywin && attackstate == Game_state.myattack)
            state = Game_state.enemydraw;
        enemyplaysupport = null;
        enemytrash.addAll(trm2);
        trm2.clear();
    }

    void mynewsummmons() {
        Card check, putsummon;
        for (int i = 0; i < myhands.size(); i++) {
            if (displaywidth / myhands.size() * i < touchx && touchx < displaywidth / myhands.size() * i + 118 && 1200 < touchy && touchy < 1392) {
                check = myhands.get(i);
                Class cls = check.getClass();
                if (cls == MonsterCard.class) {
                    MonsterCard m;
                    m = (MonsterCard) check;
                    if (m.rank <= my_rank + 1) {   /*0なら*/
                        Vector<Card> s = new Vector<Card>();
                        putsummon = myhands.remove(i);
                        myplaysummons = (MonsterCard) putsummon;
                        mysummonsdead = false;
                        state = Game_state.setsupport;
                        my_rank+=1;
                    }
                }
            }
        }
        if(touchy < 300){
            Random random = new Random();
            int ran = random.nextInt(myhands.size());
            mytrash.add(myhands.remove(ran));
            mysummonsdead = false;
            state = Game_state.setsupport;
            my_rank+=1;
        }
        my_HP = myplaysummons.HP;
        my_x = myplaysummons.x;
        my_y = myplaysummons.y;
        my_z = myplaysummons.z;
        my_color = myplaysummons.summonscolor;
    }

    void enemynewsummons() {
        Card check, putsummon;
        Vector<Card> s = new Vector<Card>();
        Class cls;
        for (int i = enemy_rank + 1; i >= 0; i--) {
            for (int k = 0; k < enemyhands.size(); k++) {
                check = enemyhands.get(k);
                cls = check.getClass();
                if (cls == MonsterCard.class) {
                    MonsterCard n = (MonsterCard) check;
                    if (n.rank == i) {
                        s.addElement(enemyhands.remove(k));
                    }
                }

            }
        }
        Random random = new Random();
        if(s.size() != 0) {
            int ran = random.nextInt(s.size());
            putsummon = s.remove(ran);
            enemyplaysummons = (MonsterCard) putsummon;
            enemyhands.addAll(s);
        }
        else if(s.size() != 0){
            int ran = random.nextInt(enemyhands.size());
            enemytrash.add(enemyhands.remove(ran));
        }
        enemy_HP = enemyplaysummons.HP;
        enemy_x = enemyplaysummons.x;
        enemy_y = enemyplaysummons.y;
        enemy_z = enemyplaysummons.z;
        enemy_color = enemyplaysummons.summonscolor;
        enemysummonsdead = false;
        enemy_rank += 1;
        state = Game_state.setsupport;
    }

    void mysetsupport() {
        Card check;
        for (int i = 0; i < myhands.size(); i++) {
            if (displaywidth / myhands.size() * i < touchx && touchx < displaywidth / myhands.size() * i + 118 && 1200 < touchy && touchy < 1392) {
                check = myhands.get(i);
                Class cls = check.getClass();
                if (cls == SupportCard.class) {
                    Card sup = myhands.elementAt(i);
                    trm1.add(myhands.remove(i));
                    myplaysupport = (SupportCard) sup;
                    if (attackstate == Game_state.myattack) state = Game_state.battle;
                    if (attackstate == Game_state.enemyattack) wait(4, 10);
                }
            }
        }
        if (touchy > 1400) {
            if (attackstate == Game_state.myattack) state = Game_state.battle;
            if (attackstate == Game_state.enemyattack) wait(4, 10);
        }
    }

    void enemysetsupport() {
            Card check;
            Class cls;
            Vector<Card> sup;
            Card putsupport;
            sup = new Vector<Card>();
            for (int k = 0; k < enemyhands.size(); k++) {
                check = enemyhands.get(k);
                cls = check.getClass();
                if (cls == SupportCard.class) {
                    sup.addElement(enemyhands.remove(k));
                }
            }
            if (sup.size() != 0) {
                if(mysummonsdead == false && enemysummonsdead == false) {
                    Random random = new Random();
                    int ran = random.nextInt(sup.size());
                    putsupport = sup.elementAt(ran);
                    trm2.add(sup.remove(ran));
                    enemyplaysupport = (SupportCard) putsupport;
                    enemyhands.addAll(sup);
                    sup.clear();
                }
                if (attackstate == Game_state.enemyattack) state = Game_state.battle;
                if (attackstate == Game_state.myattack) wait(3, 10);
            }
            if (sup.size() == 0) {
                if (attackstate == Game_state.enemyattack) state = Game_state.battle;
                if (attackstate == Game_state.myattack) wait(3, 10);
        }
    }

    void mysupporteffect() {
        myeffect();
        if (attackstate == Game_state.enemyattack) wait(6, 1000);
        if (attackstate == Game_state.myattack) wait(8, 1000);
    }
    void enemysupporteffect() {
        enemyeffect();
        if (attackstate == Game_state.myattack) wait(5, 1000);
        if (attackstate == Game_state.enemyattack) wait(7, 1000);
    }
    void myef(){
        myeffectsummons();
        if (attackstate == Game_state.myattack) wait(1, 1000);
        if (attackstate == Game_state.enemyattack) wait(8, 1000);
    }
    void enemyef(){
        enemyeffectsummons();
        if (attackstate == Game_state.myattack) wait(7, 1000);
        if (attackstate == Game_state.enemyattack) wait(2, 1000);
    }


    void myeffectsummons(){
        if (mysummonsdead == false && myselectbutton.equals("z")) {
            Random randam = new Random();
            int handes;
            switch (myplaysummons.ef) {
                case 0:
                    enemy_x = 0;
                    break;
                case 1: enemy_y = 0; break;
                case 2: enemy_z = 0; break;
                case 3: if(enemy_color.equals("Yellow")) my_z *= 3;
                    break;
                case 4: if(enemy_color.equals("Red")) my_z *= 3;
                    break;
                case 5: if(enemy_color.equals("Blue")) my_z *= 3;
                    break;
                case 6: if(enemy_color.equals("Green")) my_z *= 3;
                    break;
                case 7: if (mydecks.size() != 0) myhands.addElement(mydecks.remove(0)); break;
                case 8:
                    handes = randam.nextInt(enemyhands.size());
                    enemytrash.addElement(enemyhands.remove(handes));
                    break;
                case 9:
                    for(int i = 0; i < 2; i++){
                        handes = randam.nextInt(enemyhands.size());
                        enemytrash.addElement(enemyhands.remove(handes));
                    }
                    break;
            }
        }
        mybutton = true;
    }
    void enemyeffectsummons(){
        if (enemysummonsdead == false && enemyselectbutton.equals("z")) {
            Random randam = new Random();
            int handes;
            switch (enemyplaysummons.ef) {
                case 0:
                    my_x = 0;
                    break;
                case 1: my_y = 0; break;
                case 2: my_z = 0; break;
                case 3: if(my_color.equals("Yellow")) enemy_z *= 3;
                    break;
                case 4: if(my_color.equals("Red")) enemy_z *= 3;
                    break;
                case 5: if(my_color.equals("Blue")) enemy_z *= 3;
                    break;
                case 6: if(my_color.equals("Green")) enemy_z *= 3;
                    break;
                case 7: if (enemydecks.size() != 0) enemyhands.addElement(enemydecks.remove(0)); break;
                case 8:
                    handes = randam.nextInt(enemyhands.size());
                    mytrash.addElement(myhands.remove(handes));
                    break;
                case 9:
                    for(int i = 0; i < 2; i++){
                        handes = randam.nextInt(enemyhands.size());
                        enemytrash.addElement(enemyhands.remove(handes));
                    }
                    break;
            }
        }
        enemybutton = true;
    }

    void myeffect() {
        if (myplaysupport != null) {
            switch (myplaysupport.effect) {
                case 0:
                    break;
                case 1:
                    my_x += 100;
                    my_y += 100;
                    my_z += 100;
                    break;
                case 2:
                    my_x += 200;
                    break;
                case 3:
                    my_y += 200;
                    break;
                case 4:
                    my_z += 200;
                    break;
                case 8:
                    if (my_HP > 0) my_HP += 300;
                    break;
                case 9:
                    enemy_x = 0; break;
                case 10:
                    enemy_y = 0; break;
                case 11:
                    enemy_z = 0; break;
            }
        }
    }

    void enemyeffect() {
        if (enemyplaysupport != null) {
            switch (enemyplaysupport.effect) {
                case 0:
                    break;
                case 1:
                    enemy_x += 100;
                    enemy_y += 100;
                    enemy_z += 100;
                    break;
                case 2:
                    enemy_x += 200;
                    break;
                case 3:
                    enemy_y += 200;
                    break;
                case 4:
                    enemy_z += 200;
                    break;
                case 8:
                    if (enemy_HP > 0) enemy_HP += 300;
                    break;
                case 9:
                    my_x = 0; break;
                case 10:
                    my_y = 0; break;
                case 11:
                    my_z = 0; break;
            }
        }

    }

    void originattackpoint() {
        my_x = myplaysummons.x;
        my_y = myplaysummons.y;
        my_z = myplaysummons.z;
        enemy_x = enemyplaysummons.x;
        enemy_y = enemyplaysummons.y;
        enemy_z = enemyplaysummons.z;
    }

    void wait(int i, int t) {
        int actselect = i;
        TimerTask timeract0 = new Timeract(actselect);
        timer.schedule(timeract0, t);
    }
}

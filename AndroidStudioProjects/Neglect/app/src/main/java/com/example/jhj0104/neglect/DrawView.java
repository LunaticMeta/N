package com.example.jhj0104.neglect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.jhj0104.neglect.common.MyLine;
import com.example.jhj0104.neglect.common.MyLineSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhj0104 on 2016-12-07.
 */

//http://androiddeveloper.tistory.com/43
public class DrawView extends View {

    static List<TestInfo> LBinfoes = new ArrayList<>();

    static List<String> DInfoType= new ArrayList<>();
    static List<String> DRepeat= new ArrayList<>();
    static List<String> DMode= new ArrayList<>();
    static List<String> DX= new ArrayList<>();
    static List<String> DY= new ArrayList<>();
    static List<String> DlastedX= new ArrayList<>();
    static List<String> DlastedY= new ArrayList<>();


    Intent intent = ((Activity) getContext()).getIntent();
    Loop loop = (Loop) intent.getSerializableExtra("LoopData");
    int repeatNum = loop.loopNum;
    boolean isPractice = loop.Practice;

    float nowX, nowY, lastX, lastY;
    Paint mPaint;
    List<Vertex> arVertex;

    private float Width;     // 길이 기준
    private float Height;    // 전체 길이
    private float LinePixel;
    private float LineMargin;
    private float centerY;     // 기준점


    // 페인트 객체 선언
    public DrawView(Context context, AttributeSet attrSet ){
        super(context, attrSet);

        init(context);
    }

    public DrawView(Context context){
        super(context);



        init(context);
    }

    private void init(Context context) {

        arVertex = new ArrayList<>();

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float densityDpi = dm.densityDpi;
        Width = dm.widthPixels;
        Height = dm.heightPixels;
        centerY = Height / 2;

        int MaxCm = (int) ((Width * 2.54f) / densityDpi);
        if (MaxCm >= 18) LinePixel = (densityDpi * 18f) / 2.54f;
        else LinePixel = (densityDpi * ((float) MaxCm - 1f)) / 2.54f;

        //오차 보정
        LinePixel *= 0.833f;

        LineMargin = (Width - LinePixel) / 2f;

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setPathEffect(new CornerPathEffect(10));
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
    }

    /** 터치이벤트를 받는 함수 */

    // gtlee code
    private boolean bPressed = false;
    private Vertex prevVtx = new Vertex(0,0);
    private MyLineSet set;
    private List<MyLineSet> lineSets = new ArrayList<>();
    // end of gtlee code

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                if(isPractice == false){
                    DInfoType.add("LineBisection");
                    DRepeat.add(Integer.toString(repeatNum));
                }

                float[] point_down = {event.getX(), event.getY()};
                arVertex.add(new Vertex(point_down[0], point_down[1], false));

                    // gtlee code
                    bPressed = true;
                    set = new MyLineSet();
                    lineSets.add( set );
                    prevVtx = new  Vertex( point_down[0], point_down[1] );
                    // end of gtlee code

                if(isPractice == false){
                    DMode.add("1");
                    DlastedX.add(Float.toString(point_down[0]));
                    DlastedY.add(Float.toString(point_down[1]));
                    DX.add(Float.toString(point_down[0]));
                    DY.add(Float.toString(point_down[1]));
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(isPractice == false){
                    DInfoType.add("LineBisection");
                    DRepeat.add(Integer.toString(repeatNum));
                }

                float[] point_move = {event.getX(), event.getY()};
                arVertex.add(new Vertex(point_move[0], point_move[1], true));

                // gtlee code
                if( bPressed ) {
                    Vertex curVtx = new Vertex( point_move[0], point_move[1] );
                    set.addMyLine( new MyLine( prevVtx, curVtx ) );
                    prevVtx = curVtx;
                    System.out.println("move : " + prevVtx + " - " + curVtx );
                }
                // end of gtlee code

                invalidate();

                if(isPractice == false) {
                    DMode.add("2");
                    DlastedX.add(Float.toString(lastX));
                    DlastedY.add(Float.toString(lastY));
                    DX.add(Float.toString(point_move[0]));
                    DY.add(Float.toString(point_move[1]));
                }

                lastX = point_move[0];
                lastY = point_move[1];

                break;
            case MotionEvent.ACTION_UP:
                Button goNext = (Button) getRootView().findViewById(R.id.btn_goNext);
                goNext.setVisibility(VISIBLE);

                // gtlee code
                bPressed = false;
                prevVtx.set( event.getX(), event.getY() );
                System.out.println("up");
                // end of gtlee code

                break;
        }
        // onDraw() 호출
        return true;
    }

    /** 화면을 계속 그려주는 함수 */
    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.clearShadowLayer();
        canvas.drawColor(Color.WHITE); // 배경색상
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawLine(LineMargin, centerY, Width-LineMargin, centerY, paint);

        /*
        for (int i = 0; i < arVertex.size(); i++) {
            if (arVertex.get(i).draw) {
                canvas.drawLine(arVertex.get(i-1).x, arVertex.get(i-1).y, arVertex.get(i).x, arVertex.get(i).y, mPaint);

            } else {
                float[] point = {nowX, nowY};
                canvas.drawPoint(point[0], point[1], mPaint);
            }
        }
        */

        for (int i = 0; i < lineSets.size(); i++) {
            MyLineSet set = lineSets.get(i);
            for( int j = 0; j < set.getLines().size(); ++ j) {
                MyLine l = set.getLines().get(j);
                canvas.drawLine( l.getStartPt().getX(),
                        l.getStartPt().getY(),
                        l.getEndPt().getX(),
                        l.getEndPt().getY(),
                        mPaint
                );

            }
        }
    }


}
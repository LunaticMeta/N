package com.example.jhj0104.neglect;

import android.content.Context;
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
    // help by gtlee
public class DrawView extends View {

    float lastX, lastY;
    Paint mPaint;
    List<Vertex> arVertex;

    private float Width;
    private float Height;
    private float LinePixel;
    private float LineMargin;
    private float centerY;

    private boolean bPressed = false;
    private Vertex prevVtx = new Vertex(0,0);
    private MyLineSet set;
    public List<MyLineSet> lineSets = new ArrayList<>();
    private List<MyLineSet> lineSetsStatic = new ArrayList<>();
    public List<MyLineSet> getLineSets(){
        return lineSetsStatic;
    }

    public DrawView(Context context, AttributeSet attrSet){
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

        LinePixel *= 0.833f; //오차 보정(임의로 계산한 값)
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                float[] point_down = {event.getX(), event.getY()};

                    // gtlee code
                    bPressed = true;
                    set = new MyLineSet();
                    lineSets.add(set);
                    lineSetsStatic.add(set);
                    prevVtx = new  Vertex( point_down[0], point_down[1], true);
                    // end of gtlee code

                break;
            case MotionEvent.ACTION_MOVE:
                float[] point_move = {event.getX(), event.getY()};

                // gtlee code
                if( bPressed ) {
                    Vertex curVtx = new Vertex( point_move[0], point_move[1] );
                    set.addMyLine( new MyLine( prevVtx, curVtx ) );
                    prevVtx = curVtx;
                    System.out.println("move : " + prevVtx + " - " + curVtx );
                }
                // end of gtlee code

                invalidate();
                lastX = point_move[0];
                lastY = point_move[1];
                break;

            case MotionEvent.ACTION_UP:

                Button goNext = (Button) getRootView().findViewById(R.id.btn_goNext);
                goNext.setVisibility(VISIBLE);
                // gtlee code
                prevVtx.set( event.getX(), event.getY());
                System.out.println("up");
                bPressed = false;
                // end of gtlee code
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.clearShadowLayer();

        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawLine(LineMargin, centerY, Width-LineMargin, centerY, paint);

        // gtlee code
        for (int i = 0; i < lineSets.size(); i++) {
            MyLineSet set = lineSets.get(i);
            for( int j = 0; j < set.getLines().size(); ++ j) {
                MyLine l = set.getLines().get(j);
                canvas.drawLine(l.getStartPt().getX(), l.getStartPt().getY(), l.getEndPt().getX(), l.getEndPt().getY(), mPaint);
            }
        }
        // end of gtlee code
    }
}
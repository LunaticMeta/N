package com.example.jhj0104.neglect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhj0104 on 2016-12-07.
 */

//http://androiddeveloper.tistory.com/43
public class DrawView extends View {

    float nowX, nowY, lastX, lastY;

    Paint mPaint;
    List<Vertex> arVertex;

    public class Vertex {
        float x;
        float y;
        boolean draw;

        // 그리기 여부
        public Vertex(float x, float y, boolean draw) {
            this.x = x;
            this.y = y;
            this.draw = draw;
        }
    }

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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Path path = new Path();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                arVertex.add(new Vertex(event.getX(), event.getY(), false));
                lastX = nowX;
                lastY = nowY;
                nowX = event.getX();
                nowY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                arVertex.add(new Vertex(event.getX(), event.getY(), true));
                lastX = nowX;
                lastY = nowY;
                nowX = event.getX();
                nowY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Button goNext = (Button) getRootView().findViewById(R.id.btn_goNext);
                goNext.setVisibility(VISIBLE);
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

        for (int i = 0; i < arVertex.size(); i++) {
            if (arVertex.get(i).draw) {
                canvas.drawLine(arVertex.get(i-1).x, arVertex.get(i-1).y, arVertex.get(i).x, arVertex.get(i).y, mPaint);
                //canvas.drawLine(lastX, lastY, arVertex.get(i).x, arVertex.get(i).y, mPaint);
            } else {
                //canvas.drawPoint(arVertex.get(i).x, arVertex.get(i).y, mPaint);
                canvas.drawPoint(nowX, nowY, mPaint);
            }
        }
    }
}
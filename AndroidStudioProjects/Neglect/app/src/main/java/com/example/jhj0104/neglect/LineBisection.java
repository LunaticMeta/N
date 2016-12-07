package com.example.jhj0104.neglect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

public class LineBisection extends AppCompatActivity {

    private float Width;     // 길이 기준
    private float Height;    // 전체 길이
    private float cm;
    private float cm18;
    private float LinePixel;
    private float LineMargin;

    private float centerX , centerY;     // 기준점

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_line_bisection);
        AppearLine myview = new AppearLine(this);
        setContentView(myview);

    }

    class AppearLine extends View {

        public AppearLine(Context context) {
            super(context);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            float densityDpi = dm.densityDpi;
            Width = dm.widthPixels;
            Height = dm.heightPixels;

            centerX = Width / 2;
            centerY = Height / 2;
            cm = (densityDpi)/2.54f;

            int MaxCm = (int)((Width * 2.54f) / densityDpi);
            if(MaxCm >= 18) LinePixel = (densityDpi*18f)/2.54f;
            else LinePixel = (densityDpi*((float)MaxCm-2f))/2.54f;

            LineMargin = (Width-LinePixel)/2f;

        }
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            centerX = Width / 2;
            centerY = Height / 2;

            super.onSizeChanged(w, h, oldw, oldh);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = new Paint();
            Path path = new Path();
            canvas.drawColor(Color.WHITE);                       // 배경색상
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5f);
            canvas.drawLine(LineMargin, centerY, Width-LineMargin, centerY, paint);
//            canvas.drawLine(centerX + Width, centerY, centerX, centerY + Height, paint);

            super.onDraw(canvas);
        }
    }
    /*
    class AppearLine extends View{

        public AppearLine(Context context) {
            super(context);
            // HeightTriangle = 삼각형의 높이 ( 길이  * (삼각형의 가중치 / 사각형의 가중치))
            HeightTriangle = ( Width * ( WidthTriangle / WidthRect ));
            //  Height = Triangle 높이 + 길이
            Height = HeightTriangle + Width;
            // centerX = 화면 길이 / 2 - 길이  / 2
            centerX = getWidth() / 2 - Width / 2;
            // centerY = (화면 높이 / - Triangle 높이 + 길이 ) / 2
            centerY = ( getHeight() - Height ) /2;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            centerX = getWidth() / 2 - Width / 2;
            centerY = ( getHeight() - Height ) /2;

            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = new Paint();
            Path path = new Path();
            canvas.drawColor(Color.WHITE);                       // 배경색깔

            paint.setColor(Color.BLUE);
            path.moveTo(centerX + Width / 2, centerY);
            path.lineTo(centerX + Width, centerY + HeightTriangle);
            path.lineTo(centerX, centerY + HeightTriangle);
            path.lineTo(centerX + Width / 2, centerY);
            path.close();
            canvas.drawPath(path, paint);

            paint.setColor(Color.BLACK);                        // 펜색깔
            paint.setStyle(Paint.Style.STROKE);                 // STROKE 빈화면
            canvas.drawRect(centerX, centerY + HeightTriangle, centerX + Width, centerY + Height, paint);   // 사각형

            paint.setColor(Color.RED);                           // 타원 색깔
            RectF rf = new RectF(centerX, centerY + HeightTriangle, centerX + Width, centerY + Height);
            paint.setStyle(Paint.Style.FILL);                // 타원 꽉차게 색깔 입히기
            canvas.drawRoundRect(rf, rf.centerX() , rf.centerY(), paint);

            paint.setColor(Color.BLUE);                          // 라인 색깔
            paint.setStrokeWidth(3);
            canvas.drawLine(centerX, centerY + HeightTriangle, centerX + Width, centerY + Height, paint);
            canvas.drawLine(centerX + Width, centerY + HeightTriangle, centerX, centerY + Height, paint);

            super.onDraw(canvas);
        }

    }
    */


}

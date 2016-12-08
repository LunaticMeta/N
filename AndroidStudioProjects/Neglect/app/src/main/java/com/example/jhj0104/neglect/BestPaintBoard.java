package com.example.jhj0104.neglect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by jhj0104 on 2016-12-08.
 */

//doit android
    //
public class BestPaintBoard extends View {

    static List<TestInfo> LBinfoes = new ArrayList<>();

    static List<String> InfoType= new ArrayList<>();
    static List<String> Repeat= new ArrayList<>();
    static List<String> Mode= new ArrayList<>();
    static List<String> X= new ArrayList<>();
    static List<String> Y= new ArrayList<>();
    static List<String> lastedX= new ArrayList<>();
    static List<String> lastedY= new ArrayList<>();

    int repeatNum=0;

    Stack undos = new Stack();
    public static int maxUndos = 10;
    public boolean changed = false;
    Canvas mCanvas;
    Bitmap mBitmap;
    Paint mPaint;
    float lastX;
    float lastY;

    private Path mPath = new Path();

    private float mCurveEndX;
    private float mCurveEndY;

    private int mInvalidateExtraBorder = 1;
    static final float TOUCH_TOLERANCE = 2;

    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;

    private float Width;     // 길이 기준
    private float Height;    // 전체 길이
    private float LinePixel;
    private float LineMargin;
    private float centerY;     // 기준점

    public BestPaintBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public BestPaintBoard(Context context) {
        super(context);
        init(context);
    }
    public void init(Context context){

        InfoType.clear();
        Repeat.clear();
        Mode.clear();
        X.clear();
        Y.clear();
        lastedX.clear();
        lastedY.clear();

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

        // create a new paint object
        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5.0f);
        mPaint.setDither(DITHER_FLAG);

        lastX = -1;
        lastY = -1;
        Log.i("GoodPaintBoard", "initialized.");
    }

    public void clearUndo()
    {
        while(true) {
            Bitmap prev = (Bitmap)undos.pop();
            if (prev == null) return;

            prev.recycle();
        }
    }
    public void saveUndo()
    {
        if (mBitmap == null) return;

        while (undos.size() >= maxUndos){
            Bitmap i = (Bitmap)undos.get(undos.size()-1);
            i.recycle();
            undos.remove(i);
        }

        Bitmap img = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        undos.push(img);
        Log.i("GoodPaintBoard", "saveUndo() called.");
    }

    public void undo()
    {
        Bitmap prev = null;
        try {
            prev = (Bitmap)undos.pop();
        } catch(Exception ex) {
            Log.e("GoodPaintBoard", "Exception : " + ex.getMessage());
        }

        if (prev != null){
            drawBackground(mCanvas);
            mCanvas.drawBitmap(prev, 0, 0, mPaint);
            invalidate();

            prev.recycle();
        }

        Log.i("GoodPaintBoard", "undo() called.");
    }
    public void drawBackground(Canvas canvas)
    {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
        }
    }
    public void updatePaintProperty(int color, int size)
    {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(size);
    }
    public void newImage(int width, int height)
    {
        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        mCanvas = canvas;

        drawBackground(mCanvas);

        changed = false;
        invalidate();
    }
    public void setImage(Bitmap newImage)
    {
        changed = false;

        setImageSize(newImage.getWidth(),newImage.getHeight(),newImage);
        invalidate();
    }
    public void setImageSize(int width, int height, Bitmap newImage)
    {
        if (mBitmap != null){
            if (width < mBitmap.getWidth()) width = mBitmap.getWidth();
            if (height < mBitmap.getHeight()) height = mBitmap.getHeight();
        }

        if (width < 1 || height < 1) return;

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        drawBackground(canvas);

        if (newImage != null) {
            canvas.setBitmap(newImage);
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mCanvas.restore();
        }
        mBitmap = img;
        mCanvas = canvas;
        clearUndo();
    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            newImage(w, h);
        }
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        canvas.drawLine(LineMargin, centerY, Width-LineMargin, centerY, mPaint);
    }
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                changed = true;
                Rect rect = touchUp(event, false);
                if (rect != null) {
                    invalidate(rect);
                }
                mPath.rewind();

                Button goNext = (Button) getRootView().findViewById(R.id.btn_goNext);
                goNext.setVisibility(VISIBLE);
                return true;

            case MotionEvent.ACTION_DOWN:
                saveUndo();
                rect = touchDown(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;
        }
        return false;
    }
    private Rect touchDown(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        lastX = x;
        lastY = y;

        Rect mInvalidRect = new Rect();
        mPath.moveTo(x, y);
        LBinfoes.add(new TestInfo("LineBisection",repeatNum,1, x, y, lastX, lastY));
        InfoType.add("LineBisection");
        Repeat.add(Integer.toString(repeatNum));
        Mode.add("1");
        X.add(Float.toString(x));
        Y.add(Float.toString(y));
        lastedX.add(Float.toString(lastX));
        lastedY.add(Float.toString(lastY));

        final int border = mInvalidateExtraBorder;
        mInvalidRect.set((int) x - border, (int) y - border, (int) x + border, (int) y + border);

        mCurveEndX = x;
        mCurveEndY = y;
        mCanvas.drawPath(mPath, mPaint);

        return mInvalidRect;
    }



    private Rect touchMove(MotionEvent event) {
        Rect rect = processMove(event);
        return rect;
    }

    private Rect touchUp(MotionEvent event, boolean cancel) {
        Rect rect = processMove(event);
        return rect;
    }
    private Rect processMove(MotionEvent event) {

        final float x = event.getX();
        final float y = event.getY();

        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);





        Rect mInvalidRect = new Rect();
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            final int border = mInvalidateExtraBorder;
            mInvalidRect.set((int) mCurveEndX - border, (int) mCurveEndY - border, (int) mCurveEndX + border, (int) mCurveEndY + border);

            float cX = mCurveEndX = (x + lastX) / 2;
            float cY = mCurveEndY = (y + lastY) / 2;

            mPath.quadTo(lastX, lastY, cX, cY);
            //mPath.lineTo(cX, cY);
            LBinfoes.add(new TestInfo("LineBisection",repeatNum,2,cX,cY, lastX, lastY));
            InfoType.add("LineBisection");
            Repeat.add(Integer.toString(repeatNum));
            Mode.add("2");
            X.add(Float.toString(x));
            Y.add(Float.toString(y));
            lastedX.add(Float.toString(lastX));
            lastedY.add(Float.toString(lastY));

            // union with the control point of the new curve
            mInvalidRect.union((int) lastX - border, (int) lastY - border, (int) lastX + border, (int) lastY + border);

            // union with the end point of the new curve
            mInvalidRect.union((int) cX - border, (int) cY - border, (int) cX + border, (int) cY + border);

            lastX = x;
            lastY = y;

            mCanvas.drawPath(mPath, mPaint);
        }
        return mInvalidRect;
    }


}

package com.example.jhj0104.neglect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jhj0104 on 2017-01-13.
 */

public class CLineBisectionView extends View {

    static float moveX, moveY;

    public CLineBisectionView(Context context) {
        super(context);
    }
    public CLineBisectionView(Context context, AttributeSet attrSet){
        super(context, attrSet);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            moveX = event.getX();
            moveY = event.getY();
        }
        return true;
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        return;
    }
}

package com.example.jhj0104.neglect.lib.glutils;

/**
 * Created by jhj0104 on 2017-01-06.
 */

public interface IDrawer2D {
    void release();

    float[] getMvpMatrix();

    IDrawer2D setMvpMatrix(float[] var1, int var2);

    void getMvpMatrix(float[] var1, int var2);

    void draw(int var1, float[] var2, int var3);

    void draw(ITexture var1);
}

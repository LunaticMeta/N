package com.example.jhj0104.neglect.lib.glutils;

/**
 * Created by jhj0104 on 2017-01-06.
 */

import java.io.IOException;

/**
 * Created by saki on 2016/10/19.
 */

public interface ITexture {
    void release();

    void bind();

    void unbind();

    int getTexTarget();

    int getTexture();

    float[] getTexMatrix();

    void getTexMatrix(float[] var1, int var2);

    int getTexWidth();

    int getTexHeight();

    void loadTexture(String var1) throws NullPointerException, IOException;
}

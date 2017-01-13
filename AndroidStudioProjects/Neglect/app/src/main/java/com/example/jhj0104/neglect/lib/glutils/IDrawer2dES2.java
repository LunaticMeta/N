package com.example.jhj0104.neglect.lib.glutils;

/**
 * Created by jhj0104 on 2017-01-06.
 */

/**
 * Created by saki on 2016/12/10.
 *
 */
public interface IDrawer2dES2 extends IDrawer2D {
    public int glGetAttribLocation(final String name);
    public int glGetUniformLocation(final String name);
    public void glUseProgram();
}

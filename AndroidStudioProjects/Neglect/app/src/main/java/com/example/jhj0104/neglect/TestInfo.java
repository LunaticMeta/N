package com.example.jhj0104.neglect;

import java.io.Serializable;

/**
 * Created by jhj0104 on 2016-12-08.
 */

public class TestInfo implements Serializable {

    String Type;
    int Repeat;
    int Mode;
    int Time;

    float X;
    float Y;
    float lastX;
    float lastY;

    /*
    * LineBisection Mode *
    0: none;
    1: down(moveTo)
    2: move(quadTo)
    */
    public TestInfo(String Type, int Repeat, int Mode, float X, float Y){
        this.Type = Type;
        this.Repeat = Repeat;
        this.Mode = Mode;

        this.lastX = this.X;
        this.X = X;
        this.lastY = this.Y;
        this.Y = Y;

    }
    public TestInfo(String Type, int Repeat, int Mode, float X, float Y, float lastX, float lastY){
        this.Type = Type;
        this.Repeat = Repeat;
        this.Mode = Mode;

        this.lastX = lastX;
        this.X = X;
        this.lastY = lastY;
        this.Y = Y;
    }
}

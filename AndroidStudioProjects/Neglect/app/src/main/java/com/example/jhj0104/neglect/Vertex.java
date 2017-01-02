package com.example.jhj0104.neglect;

/**
 * Created by jhj0104 on 2016-12-08.
 */

public class Vertex {
    float x;
    float y;
    float time;
    boolean draw;

    public Vertex( float x, float y ){
        this.x = x;
        this.y = y;
        this.draw = false;
    }

    // 그리기 여부
    public Vertex(float x, float y, boolean draw) {
        this.x = x;
        this.y = y;
        this.draw = draw;
    }

    public float getX() {
        return x;
    }

    public void set( float x, float y ){
        this.x = x;
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public String toString(){
        String str = x+", "+y+"\n";
        return str;
    }
}
package com.example.jhj0104.neglect.common;

import com.example.jhj0104.neglect.Vertex;

/**
 * Created by jhj0104 on 2016-12-09.
 */

public class MyLine {

    private Vertex startPt;
    private Vertex endPt;

    public MyLine( float sx, float sy, float ex, float ey ){
        startPt = new Vertex(sx, sy);
        endPt = new Vertex(ex, ey);
    }

    public MyLine( Vertex s, Vertex e ){
        startPt = s;
        endPt = e;
    }

    public Vertex getIntersectVertex( MyLine line ){
        Vertex result = new Vertex(0,0);

        return null;

    }

    public Vertex getStartPt() {
        return startPt;
    }

    public void setStartPt(Vertex startPt) {
        this.startPt = startPt;
    }

    public Vertex getEndPt() {
        return endPt;
    }

    public void setEndPt(Vertex endPt) {
        this.endPt = endPt;
    }
}

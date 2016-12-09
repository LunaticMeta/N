package com.example.jhj0104.neglect.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhj0104 on 2016-12-09.
 */

public class MyLineSet {
    List<MyLine> lines = new ArrayList<MyLine>();

    public void addMyLine( MyLine l ){
        lines.add(l);
    }

    public List<MyLine> getLines() {
        return lines;
    }

    public void setLines(List<MyLine> lineSet) {
        this.lines = lineSet;
    }
}

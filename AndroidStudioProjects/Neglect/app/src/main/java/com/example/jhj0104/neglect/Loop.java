package com.example.jhj0104.neglect;

import java.io.Serializable;

/**
 * Created by jhj0104 on 2016-12-08.
 */

// 참고 : http://bitsoul.tistory.com/117

public class Loop implements Serializable {
    public String Name;
    public boolean Practice;
    public int loopNum;

    public Loop(String Name, boolean Practice){
        this.Name = Name;
        this.Practice = Practice;
        this.loopNum = 1;
    }
    public Loop(String Name, boolean Practice, int loopNum) {
        this.Name = Name;
        this.Practice = Practice;
        this.loopNum = loopNum;
    }
}





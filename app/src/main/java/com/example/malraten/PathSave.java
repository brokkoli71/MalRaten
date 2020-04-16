package com.example.malraten;

import java.util.ArrayList;

public class PathSave {
    public ArrayList<Float> x = new ArrayList<Float>();
    public ArrayList<Float> y = new ArrayList<Float>();
    public int color;

    public void addX(float x){
        this.x.add(x);
    }
    public void addY(float y){
        this.y.add(y);
    }

    public void clear(){
        x.clear();
        y.clear();
    }
}

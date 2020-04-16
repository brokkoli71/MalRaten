package com.example.malraten;

import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class StringTransformator {

    public static boolean withColor = true;

    static ArrayList<FingerPath> getFingerPathList(String string){
        ArrayList<FingerPath> paths = new ArrayList<>();
        String[] aS = string.split("\\{");

        for (String s : aS) {
            if (!s.equals("")) {
                int iX = s.indexOf("x[") + 2;
                String[] sX = s.substring(iX, s.indexOf("]", iX)).split(";");

                int iY = s.indexOf("y[") + 2;
                String[] sY = s.substring(iY, s.indexOf("]", iY)).split(";");

                int c, iC, iC2;
                try {
                    iC = s.indexOf("c[") + 2;
                    String sC = s.substring(iC, s.indexOf("]", iC));
                    c = Integer.parseInt(sC);
                    withColor = true;
                } catch (NumberFormatException e) {
                    c = PaintView.DEFAULT_COLOR;
                    withColor = false;
                }

                float x = Float.parseFloat(sX[0]);   //gets java.lang.NumberFormatException: empty String
                float y = Float.parseFloat(sY[0]);

                Path mPath = new Path();
                FingerPath fp = new FingerPath(c, false, false, PaintView.BRUSH_SIZE, mPath);
                paths.add(fp);

                mPath.reset();
                mPath.moveTo(x, y);

                float mX = x;
                float mY = y;
                for (int i = 1; i < sY.length - 1; i++) {
                    x = Float.parseFloat(sX[i]); //gets java.lang.ArrayIndexOutOfBoundsException: length=60; index=60
                    y = Float.parseFloat(sY[i]);

                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                }
                mPath.lineTo(mX, mY);

                paths.add(fp);
            }
        }
        return paths;
    }

    static String getStringFromFingerPath(PathSave pathSave){
        String s;
        String sX = "";
        String sY = "";
        String c = "";

        for (float f: pathSave.x) {
            sX+=f+";";
        }
        for (float f: pathSave.y) {
            sY+=f+";";
        }
        c = Integer.toString(pathSave.color);

        if (withColor)
            s="x["+sX+"]y["+sY+"]c["+c+"]{";
        else
            s="x["+sX+"]y["+sY+"]{";

        return s;
    }

}

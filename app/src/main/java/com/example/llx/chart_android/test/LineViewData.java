package com.example.llx.chart_android.test;

import android.graphics.PointF;

import com.llx278.chart.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llx on 2017/1/7.
 */

public class LineViewData {
    public static void addLineView(LineChartView lineChartView) {

        List<PointF> pos = new ArrayList<>();

        PointF p0 = new PointF(3,30);
        PointF p1 = new PointF(5,45);
        PointF p2 = new PointF(7,25);
        PointF p3 = new PointF(12,10);

        pos.add(p0);
        pos.add(p1);
        pos.add(p2);
        pos.add(p3);

        lineChartView.addPoints(pos);
        lineChartView.setAxis(AxisFake.createX(), AxisFake.createY());


    }
}

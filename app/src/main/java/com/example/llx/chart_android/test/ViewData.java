package com.example.llx.chart_android.test;

import android.graphics.PointF;

import com.llx278.chart.model.Axis;
import com.llx278.chart.view.BarChartView;
import com.llx278.chart.view.BarSetChartView;
import com.llx278.chart.view.BarSetLineChartView;
import com.llx278.chart.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llx on 2017/1/7.
 */

public class ViewData {

    public static void addBarSetViewData(BarSetChartView barSetChartView) {

        List<PointF> pointFs = new ArrayList<>();
        List<PointF> pointFs1 = new ArrayList<>();
        List<PointF> pointFs2 = new ArrayList<>();

        pointFs.add(new PointF(5,5));
        pointFs.add(new PointF(5,8));
        pointFs.add(new PointF(5,13));

        pointFs1.add(new PointF(10,15));
        pointFs1.add(new PointF(10,10));
        pointFs1.add(new PointF(10,9));

        pointFs2.add(new PointF(15,10));
        pointFs2.add(new PointF(15,16));
        pointFs2.add(new PointF(15,10));

        barSetChartView.addPointsSet(pointFs,pointFs1,pointFs2);
        barSetChartView.setAxis(AxisFake.createX(), AxisFake.createY());
        barSetChartView.setIsCanTouch(false);
    }

    public static void addBarViewData(BarChartView barChartView) {

        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(5,4));
        pointFs.add(new PointF(7,20));
        pointFs.add(new PointF(9,30));

        barChartView.addPoints(pointFs);
        barChartView.setAxis(AxisFake.createX(),AxisFake.createY());
    }

    public static void addLineViewData(LineChartView lineChartView) {

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

    public static void addBarSetLineViewData(BarSetLineChartView barSetLineChartView) {

        List<PointF> pointFs = new ArrayList<>();
        List<PointF> pointFs1 = new ArrayList<>();
        List<PointF> pointFs2 = new ArrayList<>();

        pointFs.add(new PointF(5,10));
        pointFs.add(new PointF(5,10));
        pointFs.add(new PointF(5,10));

        pointFs1.add(new PointF(10,15));
        pointFs1.add(new PointF(10,10));
        pointFs1.add(new PointF(10,15));

        pointFs2.add(new PointF(15,10));
        pointFs2.add(new PointF(15,10));
        pointFs2.add(new PointF(15,10));

        barSetLineChartView.addPointsSet(pointFs,pointFs1,pointFs2);
        barSetLineChartView.setAxis(AxisFake.createX(), AxisFake.createY());

    }


}

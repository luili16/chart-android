package com.example.llx.chart_android.test;

import com.llx278.chart.model.Axis;
import com.llx278.chart.model.AxisValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llx on 2017/1/7.
 */

public class AxisFake {

    public static Axis createX() {

        List<AxisValue> axisValuesX = new ArrayList<>();
        axisValuesX.add(new AxisValue(5, Constant.a0));
        axisValuesX.add(new AxisValue(10, Constant.a1));
        axisValuesX.add(new AxisValue(15, Constant.a2));

        Axis axisX = new Axis();
        axisX.setAxisRange(0f,20f);
        axisX.setLabelTextSize(9);
        axisX.setDrawGrid(true);
        axisX.setDrawLabel(true);
        axisX.setDrawLabelAuto(true);
        axisX.setAxisThickness(1);
        axisX.setLength(4);
        axisX.setAxisValues(axisValuesX);
        axisX.setPosition(Axis.BOTTOM);
        return axisX;
    }

    public static Axis createY() {
        List<AxisValue> axisValuesY = new ArrayList<>();
        axisValuesY.add(new AxisValue(10, Constant.a0));
        axisValuesY.add(new AxisValue(20, Constant.a1));
        axisValuesY.add(new AxisValue(30, Constant.a2));

        Axis axisY = new Axis();
        axisY.setAxisRange(0,50);
        axisY.setLabelTextSize(9);
        axisY.setDrawGrid(true);
        axisY.setAxisThickness(1);
        axisY.setAxisValues(axisValuesY);
        axisY.setPosition(Axis.LEFT);

        return axisY;
    }
}

package com.llx278.chart.model;

import com.llx278.chart.util.ChartUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llx on 11/18/16.
 */

public class Axis {

    // 坐标系在坐标轴的位置
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    // 每一个label字符的最大字符数
    private int length = 4;
    // label的颜色
    private int labelColor = ChartUtils.DEFAULT_LABEL_COLOR;
    // label的文本大小
    private int labelTextSize = 14;
    /**label与坐标轴的距离，注意，这个距离是被平均分配的*/
    private int labelSeparation = 5;
    // 网格线的宽度
    private int gridThickness = 1;
    // 网格线的颜色
    private int gridColor = ChartUtils.DEFAULT_GRID_COLOR;
    // 坐标轴的宽度
    private int axisThickness = 1;
    // 坐标轴的颜色
    private int axisColor = ChartUtils.DEFAULT_AXIS_COLOR;
    // 坐标范围的最小值
    private float minimumValue;
    // 坐标范围最大值
    private float maximumValue;
    // 是否画坐标轴
    private boolean drawAxis = true;
    // 是否画网格
    private boolean drawGrid = false;
    // 是否画标签
    private boolean drawLabel = true;
    /*是否自动画坐标系的标签，如果为true的话，将会
      *根据坐标范围找出相对合理的标签位置。如果为false
      *话将不会画标签
     */
    private boolean drawLabelAuto = true;
    // 用户自定义的标签内容
    private List<AxisValue> axisValues = new ArrayList<>();

    private int position = -1;

    public void setAxisRange(float min,float max) {

        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }

        this.minimumValue = min;
        this.maximumValue = max;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public int getLabelSeparation() {
        return labelSeparation;
    }

    public void setLabelSeparation(int labelSeparation) {
        this.labelSeparation = labelSeparation;
    }

    public int getGridThickness() {
        return gridThickness;
    }

    public void setGridThickness(int gridThickness) {
        this.gridThickness = gridThickness;
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
    }

    public int getLabelTextSize() {
        return labelTextSize;
    }

    public void setLabelTextSize(int labelTextSize) {
        this.labelTextSize = labelTextSize;
    }

    public float getMinimumValue() {
        return minimumValue;
    }

    public float getMaximumValue() {
        return maximumValue;
    }

    public boolean isDrawAxis() {
        return drawAxis;
    }

    public void setDrawAxis(boolean drawAxis) {
        this.drawAxis = drawAxis;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
    }

    public boolean isDrawLabelAuto() {
        return drawLabelAuto;
    }

    public void setDrawLabelAuto(boolean drawLabelAuto) {
        this.drawLabelAuto = drawLabelAuto;
    }

    public int getAxisThickness() {
        return axisThickness;
    }

    public void setAxisThickness(int axisThickness) {
        this.axisThickness = axisThickness;
    }

    public int getAxisColor() {
        return axisColor;
    }

    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    public List<AxisValue> getAxisValues() {
        return axisValues;
    }

    public void setAxisValues(List<AxisValue> axisValues) {
        this.axisValues = axisValues;
    }

    public boolean isDrawLabel() {
        return drawLabel;
    }

    public void setDrawLabel(boolean drawLabel) {
        this.drawLabel = drawLabel;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

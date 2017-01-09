package com.llx278.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.llx278.chart.util.ChartUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llx on 11/25/16.
 */

public class BarChartView extends BaseChartView {

    /**
     * 矩形间的默认最小间距单位 dp
     */
    private static final int DEFAULT_MIN_INTERVAL = 5;

    private List<PointF> mPoints = new ArrayList<>();

    // 当前坐标系内数据集合的rect
    protected RectF mDataRect = new RectF();
    private Paint mBarPaint;
    // 矩形间的最小的间距的值单位 dp
    private int mMinInterval;


    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mMinInterval = ChartUtils.dp2px(mDensity,DEFAULT_MIN_INTERVAL);
    }

    public void addPoints(@NonNull List<PointF> points) {
        mPoints.addAll(points);
        computePosition();
    }

    public void addPoints(@NonNull PointF point) {
        mPoints.add(point);
        computePosition();
    }

    /**
     * 设置矩形间的最小的间距间隔
     * @param minInterval 最小的间隔 单位是dp
     */
    public void setDefaultMinInterval(int minInterval) {
        mMinInterval = ChartUtils.dp2px(mDensity,minInterval);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBar(canvas);
    }

    private void computePosition() {

        List<PointF> pointFs = mPoints;
        if (pointFs == null || pointFs.isEmpty()) {
            return;
        }

        // 找到xBarPosition和yBarPosition在坐标系中所占用的范围，用一个矩形来表示
        float minimumXPosition,maximumXPosition,minimumYPosition,maximumYPosition;
        minimumXPosition = mPoints.get(0).x;
        maximumXPosition = mPoints.get(0).x;
        minimumYPosition = mPoints.get(0).y;
        maximumYPosition = mPoints.get(0).y;
        for (PointF aMBarPosition : mPoints) {
            if (minimumXPosition > aMBarPosition.x) {
                minimumXPosition = aMBarPosition.x;
            }
            if (maximumXPosition < aMBarPosition.x) {
                maximumXPosition = aMBarPosition.x;
            }
            if (minimumYPosition > aMBarPosition.y) {
                minimumYPosition = aMBarPosition.y;
            }
            if (maximumYPosition < aMBarPosition.y) {
                maximumYPosition = aMBarPosition.y;
            }
        }
        mDataRect.set(minimumXPosition,minimumYPosition,maximumXPosition,maximumYPosition);
    }

    private void drawBar(Canvas canvas) {
        float width = getDrawX(mDataRect.right) - getDrawX(mDataRect.left);
        if (mPoints.isEmpty()) {
            return;
        }
        // 矩形最大可用的宽度
        float maxInterval = width / (float)(mPoints.size() - 1);
        // 坐标系中坐标值的间隔
        float axisXInterval = getAxisXLabelInterval();
        float span = maxInterval > axisXInterval ? axisXInterval / 2 :
                (float) ((maxInterval - mMinInterval) / 2);

        // draw bar
        for (int i = 0; i< mPoints.size(); i++) {
            mBarPaint.setColor(ChartUtils.pickColor());
            float left = getDrawX(mPoints.get(i).x) - span;
            float top = mPoints.get(i).y < 0 ? getDrawY(0) : getDrawY(mPoints.get(i).y);
            float right = getDrawX(mPoints.get(i).x) + span;
            float bottom = mPoints.get(i).y < 0 ? getDrawY(mPoints.get(i).y) : getDrawY(0);
            canvas.drawRect(left,top,right,bottom,mBarPaint);
        }
    }
}

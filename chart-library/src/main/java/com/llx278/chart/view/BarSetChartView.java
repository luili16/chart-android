package com.llx278.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.llx278.chart.util.ChartUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by llx on 11/29/16.
 */

public class BarSetChartView extends BaseChartView {

    /**
     * 矩形间的默认最小间距单位 dp
     */
    private static final int DEFAULT_MIN_INTERVAL = 10;

    private List<List<PointF>> mPointsSet = new ArrayList<>();
    private Paint mBarPaint;
    private float mMinimumXPosition;
    private float mMaximumXPosition;

    // 矩形集合间的最小的间距的值单位 dp
    private int mMinInterval;

    public BarSetChartView(Context context) {
        super(context);
        init();
    }

    public BarSetChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarSetChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mMinInterval = ChartUtils.dp2px(mDensity,DEFAULT_MIN_INTERVAL);
    }

    /**
     * 添加点的集合，注意，每一个pointsSet中的x值一定要相同，
     * 这样才可以构建出合理的柱状图
     * @param pointsSet
     */
    @SafeVarargs
    public final void addPointsSet(@NonNull List<PointF>... pointsSet) {
        Collections.addAll(mPointsSet,pointsSet);
        computePosition();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSetBar(canvas);
    }

    private void drawSetBar(Canvas canvas) {

        float width = getDrawX(mMaximumXPosition) - getDrawX(mMinimumXPosition);
        if (mPointsSet.isEmpty()) {
            return;
        }

        // 矩形最大的宽度
        float maxInterval = width / (mPointsSet.size() - 1) - mMinInterval;

        for (List<PointF> pointFs : mPointsSet) {
            float outLeft = getDrawX(pointFs.get(0).x) - maxInterval / 2;
            float innerMaxInterval = maxInterval / pointFs.size();
            for (PointF pointF : pointFs) {
                mBarPaint.setColor(ChartUtils.pickColor());
                float top = pointF.y < 0 ? getDrawY(0) : getDrawY(pointF.y);
                float bottom = pointF.y < 0 ? getDrawY(pointF.y) : getDrawY(0);
                canvas.drawRect(outLeft,top,outLeft+innerMaxInterval,bottom,mBarPaint);
                outLeft += innerMaxInterval;
            }
        }
    }

    private void computePosition() {

        if (mPointsSet.isEmpty()) {
            return;
        }

        // 找到xBarPosition和yBarPosition在坐标系中所占用的范围，用一个矩形来表示
        float minimumXPosition,maximumXPosition,minimumYPosition,maximumYPosition;
        minimumXPosition = mPointsSet.get(0).get(0).x;
        maximumXPosition = mPointsSet.get(0).get(0).x;

        for (List<PointF> pointFs : mPointsSet) {
            if (minimumXPosition > pointFs.get(0).x) {
                minimumXPosition = pointFs.get(0).x;
            }
            if (maximumXPosition < pointFs.get(0).x) {
                maximumXPosition = pointFs.get(0).x;
            }
        }
        mMinimumXPosition = minimumXPosition;
        mMaximumXPosition = maximumXPosition;
    }

}

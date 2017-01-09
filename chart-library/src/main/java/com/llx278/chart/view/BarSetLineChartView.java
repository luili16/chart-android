package com.llx278.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

import com.llx278.chart.util.ChartUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by llx on 2017/1/7.
 */

public class BarSetLineChartView extends BaseChartView {

    private static final int DEFAULT_MIN_INTERVAL = 10;

    private List<List<PointF>> mPointsSet = new ArrayList<>();
    private Paint mBarPaint;
    private float mMinimumXPosition;
    private float mMaximumXPosition;

    // 矩形集合间的最小间距单位 dp
    private int mMinInterval;

    public BarSetLineChartView(Context context) {
        super(context);
        init();
    }

    public BarSetLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarSetLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSetLine(canvas);
    }

    private void drawSetLine(Canvas canvas) {
        Log.d("barSetChartView","drawSetrLine");
        float width = getDrawX(mMaximumXPosition) - getDrawX(mMinimumXPosition);
        if (mPointsSet.isEmpty()) {
            return;
        }

        // 矩形最大的宽度
        float maxInterval = width / (float)(mPointsSet.size() - 1);
        // 坐标系中坐标值的间隔
        float axisXInterval = getAxisXLabelInterval();
        float span = maxInterval > axisXInterval ? axisXInterval / 2 :
                (float) ((maxInterval - mMinInterval) / 2);

        for (List<PointF> pointFs : mPointsSet) {
            float left = getDrawX(pointFs.get(0).x) - span;
            float right = getDrawX(pointFs.get(0).x) + span;
            float top = getDrawY(0);
            float bottom = getDrawY(0);
            float y = 0;
            for (int i = 0; i < pointFs.size(); i++) {
                y = i == 0 ? pointFs.get(i).y : y + pointFs.get(i).y;
                if (y > 0) {
                    bottom = top;
                    top = getDrawY(y);
                } else {
                    top = bottom;
                    bottom = getDrawY(y);
                }
                mBarPaint.setColor(ChartUtils.pickColor());
                canvas.drawRect(left,top,right,bottom,mBarPaint);
            }
        }
    }
}

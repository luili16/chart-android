package com.llx278.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.llx278.chart.util.ChartUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llx on 2017/1/7.
 */

public class LineChartView extends BaseChartView {

    private List<PointF> mPoints = new ArrayList<>();

    private Paint mLinePaint;

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(ChartUtils.dp2px(mDensity,2));
        mLinePaint.setColor(ChartUtils.pickColor());
    }

    public void addPoints(@NonNull List<PointF> points) {
        mPoints.addAll(points);
    }

    public void addPoints(@NonNull PointF point) {
        mPoints.add(point);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getDrawX(mPoints.get(0).x),getDrawY(mPoints.get(0).y));
        for (PointF pf : mPoints) {
            path.lineTo(getDrawX(pf.x),getDrawY(pf.y));
        }

        canvas.drawPath(path,mLinePaint);
    }
}

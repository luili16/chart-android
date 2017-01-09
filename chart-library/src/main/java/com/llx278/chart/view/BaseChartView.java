package com.llx278.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import com.llx278.chart.model.Axis;
import com.llx278.chart.model.AxisValue;
import com.llx278.chart.util.AxisAutoValues;
import com.llx278.chart.util.ChartUtils;
import com.llx278.chart.util.FloatUtils;

/**
 * 实现了画图所需要的坐标系，及其触摸事件，子类需要实现真正的绘制逻辑
 * Created by llx on 11/25/16.
 */

public class BaseChartView extends View {

    private static final String TAG = "AbstractChartView";

    /**初始的Rect*/
    private static final Rect ZERO_BOUNDS_RECT = new Rect();
    private static final Rect ZERO_CHART_RECT = new Rect();
    private static final RectF ZERO_RECTF = new RectF();
    private static final float ZOOM_AMOUNT = 0.25f;

    /**用来measure每个label的宽度*/
    private static final String MAX_CHAR = "0000000000";
    /**每个label最多可包含的字符数，如果超出此数会抛出异常*/
    private static final int MAX_LABEL_CHAR_COUNT = 10;
    /**最大的放大比例*/
    private static final int MAX_ZOOM_LEVEL = 10;

    /**绘制坐标系最大可用的Rect*/
    private Rect mBounds = ZERO_BOUNDS_RECT;
    /**可用来绘制chart的Rect,所有的labels是画在它的外面的*/
    private Rect mChartRect = ZERO_CHART_RECT;
    /**
     * 当前可用的坐标值的范围
     * note: 这里用的坐标系统与View的坐标系统相同，即与常用的数学上的坐标系统
     * y轴方向是反的，通常在屏幕上可见的坐标都是数学上的坐标系，因此对与y轴需要
     * 做一下转换，具体的转换请参考{@link #getDrawX(float)},{@link #getDrawY(float)}
     */
    private RectF mCoordinateRectF = ZERO_RECTF;

    // 当前坐标系的最大范围
    private float mMaxX;
    private float mMinX;
    private float mMaxY;
    private float mMinY;

    //当前系统的density
    protected float mDensity;
    protected float mScaleDensity;

    //x和y坐标轴
    private Axis mAxisX;
    private Axis mAxisY;

    // label的宽和高
    private int mAxisXLabelHeight;
    private int mAxisYLabelHeight;
    private int mAxisXLabelWidth;
    private int mAxisYLabelWidth;
    // label与坐标轴的间隙
    private int mAxisXLabelSeparation;
    private int mAxisYLabelSeparation;

    //当前坐标轴上x和y的坐标值
    private AxisAutoValues mAxisXStopsBuffer = new AxisAutoValues();
    private AxisAutoValues mAxisYStopsBuffer = new AxisAutoValues();

    private float[] mAxisXPositionBuffer = new float[]{};
    private float[] mAxisYPositionBuffer = new float[]{};
    private float[] mAxisXLinesBuffer = new float[]{};
    private float[] mAxisYLinesBuffer = new float[]{};
    private Point mSurfaceSizeBuffer = new Point();

    // Paint
    private Paint mAxisXTextPaint;
    private Paint mAxisYTextPaint;
    private Paint mGridXPaint;
    private Paint mGridYPaint;
    private Paint mAxisXPaint;
    private Paint mAxisYPaint;

    // 用来缓存label
    private char[] mLabelBuffer = new char[50];

    // 处理缩放相关
    private PointF mCoordinateFocus = new PointF();
    private float mLastSpanX;
    private float mLastSpanY;

    private OverScroller mScroller;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private RectF mScrollerStartViewport = new RectF(); // 仅仅在zoom和fling使用
    private Zoomer mZoomer;
    private PointF mZoomFocalPoint = new PointF();

    // 是否处理触摸事件
    // 触摸事件包括：双击，拖动，滑动，缩放
    private boolean mIsCanTouch = false;

    // 最小可用来绘制图表的大小
    protected int mMinChartSize;


    public BaseChartView(Context context) {
        super(context);
        init(context);
    }

    public BaseChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        mMinChartSize = ChartUtils.dp2px(mDensity,100);
        mScroller = new OverScroller(context);
        mScaleGestureDetector = new ScaleGestureDetector(context,mScaleGestureListener);
        mGestureDetector = new GestureDetector(context,mGestureListener);
        mZoomer = new Zoomer(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBounds.set(getPaddingLeft(), getPaddingTop(), w-getPaddingRight(), h-getPaddingBottom());
    }

    public boolean isCanTouch() {
        return mIsCanTouch;
    }

    public void setIsCanTouch(boolean isCanTouch) {
        this.mIsCanTouch = isCanTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isCanTouch()) {
            return super.onTouchEvent(event);
        }

        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal= mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(Math.max(getSuggestedMinimumWidth(),resolveSize(mMinChartSize + getPaddingLeft() + getPaddingRight(),widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),resolveSize(mMinChartSize +getPaddingTop()+getPaddingBottom(),heightMeasureSpec)));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initAxisRenderParams();

        int labelOffset;
        int labelLength;
        int endIndex;
        if (mAxisX.isDrawLabel()) {
            if (mAxisX.isDrawLabelAuto()) {
                // 画 x label
                mAxisXTextPaint.setTextAlign(Paint.Align.CENTER);
                endIndex = mAxisXStopsBuffer.valuesNumber;
                for (int i = 0; i < mAxisXStopsBuffer.valuesNumber; i++) {

                    if (FloatUtils.almostEqual(mAxisXStopsBuffer.values[i],0.0f,0.001f)) {
                        continue;
                    }

                    // 不要在需要高效率代码的地方使用String.format，例如onDraw方法，这会使绘制的性能急剧下降。
                    labelLength = FloatUtils.formatFloat(mLabelBuffer, mAxisXStopsBuffer.values[i], endIndex, mAxisXStopsBuffer.decimals, '.');
                    labelOffset = endIndex - labelLength;

                    if (mAxisX.getPosition() == Axis.TOP) {
                        canvas.drawText(mLabelBuffer, labelOffset, labelLength,
                                // 除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                mAxisXPositionBuffer[i], mChartRect.top - mAxisXLabelSeparation / 2,
                                mAxisXTextPaint);
                    } else {
                        canvas.drawText(mLabelBuffer, labelOffset, labelLength,
                                // 除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                mAxisXPositionBuffer[i], mChartRect.bottom+mAxisXLabelHeight+mAxisXLabelSeparation / 2,
                                mAxisXTextPaint);
                    }
                }
            } else if (mAxisX.getAxisValues() != null && !mAxisX.getAxisValues().isEmpty()) {
                mAxisXTextPaint.setTextAlign(Paint.Align.CENTER);
                for (int i = 0; i < mAxisX.getAxisValues().size();i++) {
                    AxisValue av = mAxisX.getAxisValues().get(i);
                    if (av != null) {
                        float value = av.getValue();
                        char[] label = av.getLabel();

                        if (mAxisX.getPosition() == Axis.TOP) {
                            canvas.drawText(label,0,label.length>mAxisX.getLength()?mAxisX.getLength():label.length,
                                    // 除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                    getDrawX(value),mChartRect.top - mAxisXLabelSeparation / 2,
                                    mAxisXTextPaint);
                        } else {
                            canvas.drawText(label,0,label.length>mAxisX.getLength()?mAxisX.getLength():label.length,
                                    //除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                    getDrawX(value),mChartRect.bottom+mAxisXLabelHeight+mAxisXLabelSeparation/2,
                                    mAxisXTextPaint);
                        }
                    }
                }
            }
        }

        if (mAxisY.isDrawLabel()) {
            if (mAxisY.isDrawLabelAuto()) {
                // 画 Y label
                mAxisYTextPaint.setTextAlign(Paint.Align.RIGHT);
                endIndex = mAxisYStopsBuffer.valuesNumber;
                for (int i = 0; i < mAxisYStopsBuffer.valuesNumber; i++) {

                    if (FloatUtils.almostEqual(mAxisYStopsBuffer.values[i],0.0f,0.001f)) {
                        continue;
                    }

                    // 不要在需要高效率代码的地方使用String.format，例如onDraw方法，这会使绘制的性能急剧下降。
                    labelLength = FloatUtils.formatFloat(mLabelBuffer, mAxisYStopsBuffer.values[i], endIndex, mAxisYStopsBuffer.decimals, '.');
                    labelOffset = endIndex - labelLength;

                    if (mAxisY.getPosition() == Axis.RIGHT) {
                        mAxisYTextPaint.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText(mLabelBuffer,
                                labelOffset,
                                labelLength,
                                // 除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                mChartRect.right+ mAxisYLabelSeparation / 2 ,
                                mAxisYPositionBuffer[i] + mAxisYLabelHeight / 4,
                                mAxisYTextPaint);
                    } else {
                        canvas.drawText(mLabelBuffer,
                                labelOffset,
                                labelLength,
                                // 除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                mChartRect.left - mAxisYLabelSeparation / 2,
                                mAxisYPositionBuffer[i] + mAxisYLabelHeight / 4,
                                mAxisYTextPaint);

                    }
                }
            } else if (mAxisY.getAxisValues()!= null && !mAxisY.getAxisValues().isEmpty()) {
                mAxisYTextPaint.setTextAlign(Paint.Align.RIGHT);
                for (int i = 0; i < mAxisY.getAxisValues().size(); i++) {
                    AxisValue av = mAxisY.getAxisValues().get(i);
                    float value = av.getValue();
                    char[] label = av.getLabel();

                    if (mAxisY.getPosition() == Axis.RIGHT) {
                        mAxisYTextPaint.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText(label,0,label.length>mAxisX.getLength()?mAxisY.getLength():label.length,
                                // 除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                mChartRect.right+ mAxisYLabelSeparation / 2,
                                getDrawY(value) + mAxisYLabelHeight / 2,mAxisYPaint);
                    } else {
                        canvas.drawText(label,0,label.length>mAxisX.getLength()?mAxisY.getLength():label.length,
                                // 除以2的目的是使label均匀的分布在坐标轴与底部的中间
                                mChartRect.left- mAxisYLabelSeparation / 2,
                                getDrawY(value) + mAxisYLabelHeight / 2,mAxisYPaint);
                    }
                }
            }
        }

        if (mAxisX.isDrawGrid()) {
            canvas.drawLines(mAxisXLinesBuffer, 0, mAxisXStopsBuffer.valuesNumber * 4, mGridXPaint);
        }

        if (mAxisY.isDrawGrid()) {
            canvas.drawLines(mAxisYLinesBuffer, 0, mAxisYStopsBuffer.valuesNumber * 4, mGridYPaint);
        }

        if (mAxisX.isDrawAxis()) {
            // x
            if (getDrawY(0) <= mChartRect.top) {
                canvas.drawLine(
                        mChartRect.left-mAxisXLabelSeparation,
                        mChartRect.top,
                        mChartRect.right+mAxisXLabelSeparation,
                        mChartRect.top,
                        mAxisXPaint);
            } else if (getDrawY(0) >= mChartRect.bottom) {
                canvas.drawLine(mChartRect.left - mAxisXLabelSeparation,
                        mChartRect.bottom,
                        mChartRect.right + mAxisXLabelSeparation,
                        mChartRect.bottom,
                        mAxisXPaint);
            } else {
                canvas.drawLine(mBounds.left,
                        getDrawY(0f),
                        mBounds.right,
                        getDrawY(0f),
                        mAxisXPaint);
            }
        }

        if (mAxisY.isDrawAxis()) {
            // y
            if (getDrawX(0) <= mChartRect.left) {
                canvas.drawLine(mChartRect.left,
                        mChartRect.top - mAxisYLabelSeparation,
                        mChartRect.left,
                        mChartRect.bottom + mAxisYLabelSeparation,
                        mAxisYPaint);
            } else if (getDrawX(0) >= mChartRect.right) {
                canvas.drawLine(mChartRect.right,
                        mChartRect.top - mAxisYLabelSeparation,
                        mChartRect.right,
                        mChartRect.bottom + mAxisYLabelSeparation,
                        mAxisYPaint);
            } else {
                canvas.drawLine(getDrawX(0),
                        mBounds.top,
                        getDrawX(0),
                        mBounds.bottom,
                        mAxisYPaint);
            }
        }
    }

    private void initAxisRenderParams() {
        computeChartRect();

        // 计算X坐标轴和y坐标轴的label在坐标轴上合理的位置
        if (mAxisX != null) {
            FloatUtils.computeAutoGeneratedAxisValues(mCoordinateRectF.left,
                    mCoordinateRectF.right,
                    mChartRect.width() / mAxisXLabelWidth / 2,
                    mAxisXStopsBuffer);
        }
        if (mAxisY != null) {
            FloatUtils.computeAutoGeneratedAxisValues(mCoordinateRectF.top,
                    mCoordinateRectF.bottom,
                    mChartRect.height() / mAxisYLabelHeight / 2,
                    mAxisYStopsBuffer);
        }

        // 避免在画的过程中无用的定位，如果label数目改变了就增加数组的长度
        if (mAxisXPositionBuffer.length < mAxisXStopsBuffer.valuesNumber) {
            mAxisXPositionBuffer = new float[mAxisXStopsBuffer.valuesNumber];
        }
        if (mAxisYPositionBuffer.length < mAxisYStopsBuffer.valuesNumber) {
            mAxisYPositionBuffer = new float[mAxisYStopsBuffer.valuesNumber];
        }
        if (mAxisXLinesBuffer.length < mAxisXStopsBuffer.valuesNumber * 4) {
            mAxisXLinesBuffer = new float[mAxisXStopsBuffer.valuesNumber * 4];
        }
        if (mAxisYLinesBuffer.length < mAxisYStopsBuffer.valuesNumber * 4) {
            mAxisYLinesBuffer = new float[mAxisYStopsBuffer.valuesNumber * 4];
        }

        // 计算label在屏幕中的位置
        for (int i = 0; i < mAxisXStopsBuffer.valuesNumber; i++) {
            mAxisXPositionBuffer[i] = getDrawX(mAxisXStopsBuffer.values[i]);
        }
        for (int i = 0; i < mAxisYStopsBuffer.valuesNumber; i++) {
            mAxisYPositionBuffer[i] = getDrawY(mAxisYStopsBuffer.values[i]);
        }

        // 计算坐标系的网格在屏幕中的位置barChartData.setPoints(axisValuesX);
        for (int i = 0; i < mAxisXStopsBuffer.valuesNumber; i++) {
            mAxisXLinesBuffer[i * 4 + 0] = (float) Math.floor(mAxisXPositionBuffer[i]);
            mAxisXLinesBuffer[i * 4 + 1] = Math.abs(mBounds.top - mChartRect.top) >
                    mAxisXLabelHeight / 2 ? mChartRect.top : mChartRect.top - mAxisYLabelSeparation;
            mAxisXLinesBuffer[i * 4 + 2] = (float) Math.floor(mAxisXPositionBuffer[i]);
            mAxisXLinesBuffer[i * 4 + 3] = Math.abs(mBounds.bottom - mChartRect.bottom) >
                    mAxisYLabelHeight / 2 ? mChartRect.bottom : mChartRect.bottom + mAxisYLabelSeparation;
        }
        for (int i = 0; i < mAxisYStopsBuffer.valuesNumber; i++) {
            mAxisYLinesBuffer[i * 4 + 0] = Math.abs(mBounds.left - mChartRect.left) >
                    mAxisXLabelWidth / 2 ? mChartRect.left : mChartRect.left-mAxisXLabelSeparation;
            mAxisYLinesBuffer[i * 4 + 1] = (float) Math.floor(mAxisYPositionBuffer[i]);
            mAxisYLinesBuffer[i * 4 + 2] = Math.abs(mBounds.right - mChartRect.right) >
                    mAxisXLabelWidth / 2 ? mChartRect.right : mChartRect.right+mAxisXLabelSeparation;
            mAxisYLinesBuffer[i * 4 + 3] = (float) Math.floor(mAxisYPositionBuffer[i]);
        }

        /*Log.d(TAG, "mAxisXPositionBuffer[] = " + Arrays.toString(mAxisXPositionBuffer) +
                "\nmAxisYPositionBuffer[] = " + Arrays.toString(mAxisYPositionBuffer) +
                "\nchartRect: " +
                "\nleft = " + mChartRect.left +
                " right = " + mChartRect.right +
                " top = " + mChartRect.top +
                " bottom = " + mChartRect.bottom + " mCoordinateRectF:" +
                "\nleft = " + mCoordinateRectF.left +
                " right = " + mCoordinateRectF.right +
                " top = " + mCoordinateRectF.top +
                " bottom = " + mCoordinateRectF.bottom);*/
    }

    private void computeChartRect() {

        if (mBounds.left == ZERO_BOUNDS_RECT.left &&
                mBounds.top == ZERO_BOUNDS_RECT.top &&
                mBounds.right == ZERO_BOUNDS_RECT.top &&
                mBounds.bottom == ZERO_BOUNDS_RECT.bottom) {
            return;
        }

        if (mAxisX == null || mAxisY == null) {
            return;
        }

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        //float rangeX = mAxisX.getMaximumValue() - mAxisX.getMinimumValue();
        //float rangeY = mAxisY.getMaximumValue() - mAxisY.getMinimumValue();
        float offsetX = mAxisXLabelWidth + mAxisXLabelSeparation;
        float offsetY = mAxisYLabelHeight + mAxisYLabelSeparation;

        // 可用来绘制的rect太小
        if (mBounds.width() <= 2 * offsetX || mBounds.height() <= 2 * offsetY) {
            Log.e(TAG,"用来绘制坐标系的空间太小 width = " + mBounds.width() + " height = " + mBounds.height());
            return;
        }

        if (mAxisY.getPosition() == Axis.LEFT) {
            left = (int) Math.floor(mBounds.left + offsetX);
        } else {
            left = mBounds.left + mAxisXLabelWidth / 2;
        }

        if (mAxisY.getPosition() == Axis.RIGHT) {
            right =  (int) Math.floor(mBounds.right - offsetX);
        } else {
            right = mBounds.right - mAxisXLabelWidth / 2;
        }

        if (mAxisX.getPosition() == Axis.BOTTOM) {
            bottom = (int) Math.floor(mBounds.bottom - offsetY);
        } else {
            bottom = mBounds.bottom - mAxisYLabelHeight / 2;
        }

        if (mAxisX.getPosition() == Axis.TOP) {
            top = (int) Math.floor(mBounds.top + offsetY);
        } else {
            top = mBounds.top + mAxisYLabelHeight / 2;
        }
        mChartRect.set(left, top, right, bottom);
    }

    /**
     * 根据x坐标轴的x的位置计算在屏幕中的位置,注意这可能会画到屏幕的外面
     * @param x
     * @return
     */
    protected float getDrawX(float x) {
        return mChartRect.left + mChartRect.width() *
                (x - mCoordinateRectF.left) / mCoordinateRectF.width();
    }

    /**
     * 根据y坐标轴的y的位置计算在屏幕中的位置，注意，这可能会画到屏幕的外面
     * @param y
     * @return
     */
    protected float getDrawY(float y) {
        return mChartRect.bottom - mChartRect.height() *
                (y - mCoordinateRectF.top) / mCoordinateRectF.height();
    }

    public void setAxis(@NonNull Axis axisX, @NonNull Axis axisY) {
       //mAxisRender.setAxis(axisX,axisY);
        mAxisX = axisX;
        mAxisY = axisY;
        computeIdealCoordinateConstraintF(mAxisX,mAxisY);
        initPaints();

        invalidate();
    }

    /**
     * 计算一个合理的坐标范围
     */
    private void computeIdealCoordinateConstraintF(Axis axisX,Axis axisY) {

        float minX = 0;
        float minY = 0;
        float maxX = 0;
        float maxY = 0;

        minX = axisX.getMinimumValue();
        minY = axisY.getMinimumValue();
        maxX = axisX.getMaximumValue();
        maxY = axisY.getMaximumValue();


        if (mMinX != minX ||
                mMinY != minY ||
                mMaxX != maxX ||
                mMaxY != maxY) {

            mMinX = minX;
            mMinY = minY;
            mMaxX = maxX;
            mMaxY = maxY;

            mCoordinateRectF.set(
                    minX,
                    minY,
                    maxX,
                    maxY
            );
        }
    }

    private void initPaints() {
        if (mAxisX != null) {
            mAxisXTextPaint = new Paint();
            mAxisXTextPaint.setAntiAlias(true);
            mAxisXTextPaint.setColor(mAxisX.getLabelColor());
            mAxisXTextPaint.setTextSize(ChartUtils.sp2px(mScaleDensity, mAxisX.getLabelTextSize()));
            mAxisXLabelHeight = (int) Math.abs(mAxisXTextPaint.getFontMetrics().top);
            if (mAxisX.getLength() <= MAX_LABEL_CHAR_COUNT) {
                mAxisXLabelWidth = (int) mAxisXTextPaint.measureText(MAX_CHAR, 0, mAxisX.getLength());
            } else {
                throw new IllegalStateException("AxisRender的标签支持" + MAX_LABEL_CHAR_COUNT + "个字符" + "当前字符AxisX=" + mAxisX.getLength());
            }
            mAxisXLabelSeparation = ChartUtils.dp2px(mDensity,mAxisX.getLabelSeparation());

            mGridXPaint = new Paint();
            mGridXPaint.setStrokeWidth(ChartUtils.dp2px(mDensity, mAxisX.getGridThickness()));
            mGridXPaint.setColor(mAxisX.getGridColor());
            mGridXPaint.setStyle(Paint.Style.STROKE);

            mAxisXPaint = new Paint();
            mAxisXPaint.setAntiAlias(true);
            mAxisXPaint.setStrokeWidth(ChartUtils.dp2px(mDensity, mAxisX.getAxisThickness()));
            mAxisXPaint.setColor(mAxisX.getAxisColor());
            mAxisXPaint.setStyle(Paint.Style.STROKE);
        }

        if (mAxisY != null) {
            mAxisYTextPaint = new Paint();
            mAxisYTextPaint = new Paint();
            mAxisYTextPaint.setAntiAlias(true);
            mAxisYTextPaint.setColor(mAxisY.getLabelColor());
            mAxisYTextPaint.setTextSize(ChartUtils.sp2px(mScaleDensity, mAxisY.getLabelTextSize()));
            mAxisYLabelHeight = (int) Math.abs(mAxisYTextPaint.getFontMetrics().top);
            if (mAxisY.getLength() <= MAX_LABEL_CHAR_COUNT) {
                mAxisYLabelWidth = (int) mAxisYTextPaint.measureText(MAX_CHAR, 0, mAxisY.getLength());
            } else {
                throw new IllegalStateException("AxisRender的标签支持" + MAX_LABEL_CHAR_COUNT + "个字符" + "当前字符AxisY=" + mAxisX.getLength());
            }
            mAxisYLabelSeparation = ChartUtils.dp2px(mDensity,mAxisY.getLabelSeparation());

            mGridYPaint = new Paint();
            mGridYPaint.setStrokeWidth(ChartUtils.dp2px(mDensity, 1));
            mGridYPaint.setColor(ChartUtils.DEFAULT_GRID_COLOR);
            mGridYPaint.setStyle(Paint.Style.STROKE);

            mAxisYPaint = new Paint();
            mAxisYPaint.setAntiAlias(true);
            mAxisYPaint.setStrokeWidth(ChartUtils.dp2px(mDensity, mAxisY.getAxisThickness()));
            mAxisYPaint.setColor(mAxisY.getAxisColor());
            mAxisYPaint.setStyle(Paint.Style.STROKE);
        }
    }

    // 处理缩放事件
    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mLastSpanX = detector.getCurrentSpanX();
            mLastSpanY = detector.getCurrentSpanY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float spanX = detector.getCurrentSpanX();
            float spanY = detector.getCurrentSpanY();

            float newWidth = mLastSpanX / spanX * mCoordinateRectF.width();
            float newHeight = mLastSpanY / spanY * mCoordinateRectF.height();

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            hitTest(focusX,focusY,mCoordinateFocus);
            mCoordinateRectF.set(mCoordinateFocus.x - newWidth * ((focusX - mChartRect.left) / mChartRect.width()),
                    mCoordinateFocus.y - newHeight * ((mChartRect.bottom - focusY) / mChartRect.height()),
                    0,
                    0);
            mCoordinateRectF.right = mCoordinateRectF.left + newWidth;
            mCoordinateRectF.bottom = mCoordinateRectF.top + newHeight;
            constrainCoordinate();
            mLastSpanX = spanX;
            mLastSpanY = spanY;
            ViewCompat.postInvalidateOnAnimation(BaseChartView.this);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };

    private GestureDetector.SimpleOnGestureListener mGestureListener =
            new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {

            mScroller.forceFinished(true);

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG,"onDoubleTap");
            mZoomer.forceFinished(true);
            if (hitTest(e.getX(),e.getY(),mZoomFocalPoint)) {
                mZoomer.startZoom(ZOOM_AMOUNT);
            }
            ViewCompat.postInvalidateOnAnimation(BaseChartView.this);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float viewportOffsetX = (distanceX / mChartRect.width()) * mCoordinateRectF.width();
            // -distanceY是因为手指滚动的方向与实际的坐标系方向是相反的
            float viewportOffsetY = (-distanceY / mChartRect.height()) * mCoordinateRectF.height();
            //computeScrollSurfaceSize(mSurfaceSizeBuffer);

            setViewportBottomLeft(mCoordinateRectF.left + viewportOffsetX,
                    mCoordinateRectF.bottom + viewportOffsetY);
            ViewCompat.postInvalidateOnAnimation(BaseChartView.this);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            fling((int)-velocityX,(int)-velocityY);
            return true;
        }
    };

    private void fling(int velocityX,int velocityY) {

        // fling use math in pixels (as opposed tr);
        computeScrollSurfaceSize(mSurfaceSizeBuffer);
        mScrollerStartViewport.set(mCoordinateRectF);

        int startX = (int) (mSurfaceSizeBuffer.x *
                ((mScrollerStartViewport.left - mMinX) / (mMaxX - mMinX)));
        int startY = (int) (mSurfaceSizeBuffer.y *
                ((mMaxY - mScrollerStartViewport.bottom) / (mMaxY - mMinY)));
        mScroller.forceFinished(true);
        mScroller.fling(startX,
                startY,
                velocityX,
                velocityY,
                0,
                mSurfaceSizeBuffer.x - mChartRect.width(),
                0,
                mSurfaceSizeBuffer.y - mChartRect.height()
                );

        ViewCompat.postInvalidateOnAnimation(this);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();

        boolean needInvalidate = false;

        if (mScroller.computeScrollOffset()) {
            // the scroller isn't finished, meaning a fling or programmatic pan operation is
            // currently active
            computeScrollSurfaceSize(mSurfaceSizeBuffer);
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();

            float currXRange = mMinX + (mMaxX - mMinX) * ((float) currX / (float) mSurfaceSizeBuffer.x);
            float currYRange = mMaxY - (mMaxY - mMinY) * ((float) currY / (float) mSurfaceSizeBuffer.y);

            setViewportBottomLeft(currXRange,currYRange);
            needInvalidate = true;
        }

        if (mZoomer.computeZoom()) {
            mScrollerStartViewport.set(mCoordinateRectF);
            float newWidth = (1f - mZoomer.getCurrentZoom()) * mScrollerStartViewport.width();
            float newHeight = (1f - mZoomer.getCurrentZoom()) * mScrollerStartViewport.height();
            float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left)
                    / mScrollerStartViewport.width();
            float pointWithinViewportY = (mZoomFocalPoint.y - mScrollerStartViewport.top)
                    / mScrollerStartViewport.height();
            mCoordinateRectF.set(mZoomFocalPoint.x - newWidth * pointWithinViewportX,
                    mZoomFocalPoint.y - newHeight * pointWithinViewportY,
                    mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX),
                    mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY)
                    );
            constrainCoordinate();
            needInvalidate = true;
        }

        if (needInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 设置当前的viewportRectF为给定的X和y的位置，注意，这里面Y值代表着最顶端的pixel的位置，
     * @param x
     * @param y
     */
    private void setViewportBottomLeft(float x, float y) {

        // 在这里面要限制滚动的范围，滚动的范围的极限值为mCoordinateRectF的最大面积

        float curWidth = mCoordinateRectF.width();
        float curHeight = mCoordinateRectF.height();
        x = Math.max(mMinX,Math.min(x,mMaxX - curWidth));
        y = Math.max(mMinY + curHeight,Math.min(y,mMaxY));

        mCoordinateRectF.set(x,y-curHeight,x+curWidth,y);
    }

    /**
     * 找到像素点(focusX,focusY)（其坐标点应该落在{@link #mChartRect}内，如果没有，返回false）
     * 所对应的坐标系中点是否在{@link #mCoordinateRectF}内，
     * 如果找到此点，则将此点设置在dest中，并且返回true，没有找到返回false
     */
    private boolean hitTest(float focusX, float focusY, PointF dest) {
        if (!mChartRect.contains((int)focusX,(int)focusY)) {
            return false;
        }

        dest.set(mCoordinateRectF.left +
                        mCoordinateRectF.width() *
                                ((focusX - mChartRect.left) / mChartRect.width()),
                mCoordinateRectF.top +
                        mCoordinateRectF.height() *
                                ((focusY - mChartRect.bottom) / -mChartRect.height()));
        return true;
    }

    /**
     * 确保当前的坐标系的范围不会超过{@link #mCoordinateRectF}
     */
    private void constrainCoordinate() {

        // 最大不能超过 mMinx,mMinY,mMaxX,mMaxy所限定的范围
        mCoordinateRectF.left = Math.max(mMinX,mCoordinateRectF.left);
        mCoordinateRectF.top = Math.max(mMinY,mCoordinateRectF.top);
        mCoordinateRectF.right = Math.min(mMaxX,mCoordinateRectF.right);
        mCoordinateRectF.bottom = Math.min(mMaxY,mCoordinateRectF.bottom);

        // 最小不能小于 MAX_ZOOM_LEVEL 确定的级别
        float minWidth = (mMaxX - mMinX) / MAX_ZOOM_LEVEL;
        float minHeight = (mMaxY - mMinY) / MAX_ZOOM_LEVEL;
        if (mCoordinateRectF.width() < minWidth) {
            mCoordinateRectF.right = mCoordinateRectF.left + minWidth;
        }

        if (mCoordinateRectF.height() < minHeight) {
            mCoordinateRectF.bottom = mCoordinateRectF.top + minHeight;
        }
    }

    /**
     * 计算当前可滚动的范围，用pixel表示，例如：如果当前没有缩放，那{@link #mChartRect}所显示的就是全部的内容，
     * 如果当前缩放了200%，那{@link #mChartRect}所显示的就是被缩放后的内容，而全部的内容则是mChartRect的两倍，
     * out返回了全部内容的宽和高
     * @param out
     */
    private void computeScrollSurfaceSize(Point out) {
        out.set((int)(mChartRect.width() *
                        ((mMaxX - mMinX) / mCoordinateRectF.width())),
                (int)(mChartRect.height() *
                        ((mMaxY - mMinY) / mCoordinateRectF.height())));
    }

    /**
     * 获得x坐标轴坐标值的间隔
     * @return
     */
    protected float getAxisXLabelInterval() {
        return Math.abs(mAxisXPositionBuffer[0] - mAxisXPositionBuffer[1]);
    }

    protected float getAxisYLabelInterval() {
        return Math.abs(mAxisYPositionBuffer[0] - mAxisYPositionBuffer[1]);
    }
}

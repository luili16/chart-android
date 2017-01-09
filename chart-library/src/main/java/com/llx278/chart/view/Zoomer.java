package com.llx278.chart.view;

import android.content.Context;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 *  一个简单的帮助类，用来处理双击缩放的手势，与{@link android.widget.Scroller}
 *  功能是类似的
 * Created by llx on 2016/12/31.
 */
public class Zoomer {

    /**
     * 插值计算器，用来使zoom的过程看起来更加的自然
     */
    private Interpolator mInterpolator;

    /**
     * 整个zoom过程中的总时长
     */
    private int mAnimationDurationMillis;

    /**
     * 是否当前的zoom过程已经完成了
     */
    private boolean mFinished = true;

    /**
     * 当前zoom的值
     */
    private float mCurrentZoom;

    /**
     * zoom开始的时间
     */
    private long mStartRTC;

    /**
     * Zoom结束时候的位置
     */
    private float mEndZoom;

    public Zoomer(Context context) {

        mInterpolator = new DecelerateInterpolator();
        mAnimationDurationMillis = context.getResources().
                getInteger(android.R.integer.config_shortAnimTime);

    }

    /**
     * 强制结束zoom的状态，并将zoom值设置为当前值
     * @param finished
     */
    public void forceFinished(boolean finished) {
        mFinished = finished;
    }

    /**
     * 终止动画，设置zoom值为结束的值
     */
    public void abortAnimation() {
        mFinished = true;
        mCurrentZoom = mEndZoom;
    }

    /**
     *  开始zoom的过程
     * @param endZoom
     */
    public void startZoom(float endZoom) {
        mStartRTC = SystemClock.elapsedRealtime();
        mEndZoom = endZoom;

        mFinished = false;
        mCurrentZoom = 1f;
    }

    /**
     * 计算当前的zoom等级，如果zoom过程仍在进行则返回true，如果zoom已经结束了则返回false
     * @return
     */
    public boolean computeZoom() {
        if (mFinished) {
            return false;
        }
        long tRTC = SystemClock.elapsedRealtime() - mStartRTC;
        if (tRTC >= mAnimationDurationMillis) {
            mFinished = true;
            mCurrentZoom = mEndZoom;
            return false;
        }

        float t = tRTC * 1f / mAnimationDurationMillis;
        mCurrentZoom = mEndZoom * mInterpolator.getInterpolation(t);
        return true;
    }

    /**
     * 返回当前的zoom等级
     * @return
     */
    public float getCurrentZoom() {
        return mCurrentZoom;
    }

}

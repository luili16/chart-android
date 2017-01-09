package com.llx278.chart.model;

import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * 坐标值，
 * Created by llx on 11/16/16.
 */

public class AxisValue implements Comparable<AxisValue> {

    private float mValue;
    private char[] mLabel;

    public AxisValue(float value) {
        mValue = value;
    }

    public AxisValue(float value,char[] label) {
        mValue = value;
        mLabel = label;
    }

    public char[] getLabel() {
        return mLabel;
    }

    public void setLabel(char[] label) {
        mLabel = label;
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
        mValue = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AxisValue axisValue = (AxisValue) o;

        if (Float.compare(axisValue.mValue, mValue) != 0) return false;
        return Arrays.equals(mLabel, axisValue.mLabel);

    }

    @Override
    public int hashCode() {
        int result = (mValue != +0.0f ? Float.floatToIntBits(mValue) : 0);
        result = 31 * result + Arrays.hashCode(mLabel);
        return result;
    }

    @Override
    public int compareTo(@NonNull AxisValue o) {

        if (this.mValue < o.mValue) {
            return -1;
        }
        if (this.mValue > o.mValue) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return String.valueOf(mValue);
    }
}

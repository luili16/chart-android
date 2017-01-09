package com.example.llx.chart_android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.llx.chart_android.test.ViewData;
import com.llx278.chart.view.LineChartView;

/**
 * Created by llx on 2017/1/7.
 */

public class LineFragment extends Fragment {

    public static LineFragment newInstance() {

        Bundle args = new Bundle();

        LineFragment fragment = new LineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LineChartView lineChartView = new LineChartView(container.getContext());
        lineChartView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ViewData.addLineViewData(lineChartView);

        return lineChartView;
    }
}

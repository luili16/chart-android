package com.example.llx.chart_android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.llx.chart_android.test.ViewData;
import com.llx278.chart.view.BarSetChartView;

/**
 * Created by llx on 2017/1/7.
 */

public class BarSetFragment extends Fragment {

    public static BarSetFragment newInstance() {
        
        Bundle args = new Bundle();
        
        BarSetFragment fragment = new BarSetFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        BarSetChartView barSetChartView = new BarSetChartView(container.getContext());

        ViewData.addBarSetViewData(barSetChartView);
        return barSetChartView;
    }
}

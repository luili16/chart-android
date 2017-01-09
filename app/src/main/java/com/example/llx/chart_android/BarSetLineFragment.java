package com.example.llx.chart_android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.llx.chart_android.test.ViewData;
import com.llx278.chart.view.BarSetLineChartView;

/**
 * Created by llx on 2017/1/9.
 */

public class BarSetLineFragment extends Fragment {

    public static BarSetLineFragment newInstance() {
        
        Bundle args = new Bundle();
        
        BarSetLineFragment fragment = new BarSetLineFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BarSetLineChartView barSetLineChartView = new BarSetLineChartView(container.getContext());
        barSetLineChartView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ViewData.addBarSetLineViewData(barSetLineChartView);
        return barSetLineChartView;
    }
}

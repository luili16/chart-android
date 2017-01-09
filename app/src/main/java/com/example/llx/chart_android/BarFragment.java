package com.example.llx.chart_android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.llx.chart_android.test.ViewData;
import com.llx278.chart.view.BarChartView;

/**
 * Created by llx on 2017/1/7.
 */

public class BarFragment extends Fragment {

    public static BarFragment newInstance() {

        Bundle args = new Bundle();

        BarFragment fragment = new BarFragment();
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

        BarChartView inflate = new BarChartView(container.getContext());
        inflate.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ViewData.addBarViewData(inflate);
        
        return inflate;
    }
}

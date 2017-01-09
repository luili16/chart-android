package com.example.llx.chart_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContainer = (FrameLayout) findViewById(R.id.main_content_container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_bar:
                BarFragment barFragment = BarFragment.newInstance();
                getFragmentManager().beginTransaction().replace(mContainer.getId(),barFragment).commit();
                break;
            case R.id.action_bar_set:
                BarSetFragment barSetFragment = BarSetFragment.newInstance();
                getFragmentManager().beginTransaction().replace(mContainer.getId(),barSetFragment).commit();
                break;
            case R.id.action_bar_line:
                LineFragment lineFragment = LineFragment.newInstance();
                getFragmentManager().beginTransaction().replace(mContainer.getId(),lineFragment).commit();
                break;
            case R.id.action_bar_set_line:
                BarSetLineFragment barSetLineFragment = BarSetLineFragment.newInstance();
                getFragmentManager().beginTransaction().replace(mContainer.getId(),barSetLineFragment).commit();
                break;
        }

        return true;
    }
}

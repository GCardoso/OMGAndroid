package com.example.guilhermecardoso.omgandroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.guilhermecardoso.omgandroid.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    Button buttonCalibrate;
    Button buttonKeyPointFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCalibrate         = (Button) this.findViewById(R.id.button_calibrate);
        buttonKeyPointFinder    = (Button) this.findViewById(R.id.button_keypoint_finder);

        buttonCalibrate.setOnClickListener(this);
        buttonKeyPointFinder.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_calibrate:
                Log.i(TAG,"Chamou Calibrar!");
            break;

            case R.id.button_keypoint_finder:
                Log.i(TAG,"Chamou Finder!");
                break;
        }
    }
}

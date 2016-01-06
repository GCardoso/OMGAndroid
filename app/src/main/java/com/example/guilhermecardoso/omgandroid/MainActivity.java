package com.example.guilhermecardoso.omgandroid;

import android.content.Intent;
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_calibrate:
                Log.i(TAG, "Chamou Calibrar!");
                Intent myIntent = new Intent(this, CameraCalibrationActivity.class);

                this.startActivity(myIntent);
            break;

            case R.id.button_keypoint_finder:
                Log.i(TAG,"Chamou Finder!");
                Intent myIntent2 = new Intent(this, KeyPointFinderActivity.class);

                this.startActivity(myIntent2);

                break;
        }
    }
}

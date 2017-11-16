package com.example.zxw.myclock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ClockView3 clockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1280x480_new);
//        clockView = (ClockView3) findViewById(R.id.clockView);
//
//        clockView.setOnTimeChangeListener(new ClockView3.OnTimeChangeListener() {
//            @Override
//            public void onTimeChange(View view, int hour, int minute, int second) {
//            }
//        });
        }
}

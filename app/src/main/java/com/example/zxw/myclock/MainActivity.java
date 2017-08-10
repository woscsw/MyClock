package com.example.zxw.myclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    ClockView clockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clockView = (ClockView) findViewById(R.id.clockView);

        clockView.setOnTimeChangeListener(new ClockView.OnTimeChangeListener() {
            @Override
            public void onTimeChange(View view, int hour, int minute, int second) {
            }
        });
        }
}

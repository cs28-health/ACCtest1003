package com.example.tsukahara.acctest1003;

import android.content.Intent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity{

    private long time_cnt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        //stopService( new Intent( MainActivity.this, test_service.class ));
        setContentView(R.layout.activity_main); // レイアウト取得
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // ツールバー取得
        // setSupportActionBar(toolbar);


        // スタートボタン押下時
        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("MainActivity", "start");
                start(); // スタート時の処理
            }
        } );

        // ストップボタン押下時
        findViewById(R.id.stop_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("MainActivity", "stop");
                stop(); // ストップ時の処理
            }
        } );

        time_cnt = 0;

    }

    public void start() {
        startService( new Intent( MainActivity.this, test_service.class ));
    }

    public void stop() {
        stopService( new Intent( MainActivity.this, test_service.class ));
    }

    @Override
    public void onDestroy() {
        Log.d("MainActivity", "onDestroy");
        super.onDestroy();
    }

}

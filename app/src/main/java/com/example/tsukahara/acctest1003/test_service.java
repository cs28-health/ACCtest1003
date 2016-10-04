package com.example.tsukahara.acctest1003;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.lang.String;

import static java.lang.Thread.sleep;

public class test_service extends Service implements SensorEventListener {

    private int cnt;
    private SensorManager manager;
    private long sum;
    private long now;
    private long past;
    private File file;
    private FileWriter outputWriter;
    private float x;
    private float y;
    private float z;
    private String x_str;
    private String y_str;
    private String z_str;
    private String xyz_str;


    public test_service() throws FileNotFoundException {
    }

    @Override
    public void onCreate() {
        Log.d("test_service", "onCreate");
        cnt = 0;
        // センサー登録
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor s = sensors.get(0);
            boolean checkbat = manager.registerListener(this, s, 10000, 10000);
            Log.d("test_service", "manager ok");
        }

        //manager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test_service", "onStartCommand");

        file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                getPresentTime(1)+"_ACC.csv");
        try {
        outputWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("test_service", "onDestroy");
        try {
            outputWriter.close();
            Log.d("test_service", "file saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.unregisterListener(this);
        Toast.makeText(this, "destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("test_service", "onBind");
        return null;

        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sum += 1;
            //now = sensorEvent.timestamp;
            //past = now;

            //long time;
            //time =  (now - past) / 1000000000;   // ns -> s
            int fps;
            fps = 30;   // 100 -> 1s  3 -> 約32Hz

            if (sum % (fps/10) == 0) {
                //Log.d("test_service", "sum: "+sum);
                x_str = String.valueOf(sensorEvent.values[0]);
                y_str = String.valueOf(sensorEvent.values[1]);
                z_str = String.valueOf(sensorEvent.values[2]);
                xyz_str = getPresentTime(0) + "," + x_str + "," + y_str + "," + z_str + "," + fps;


                // 外部ストレージに書き込み可能かチェック
                if (isExternalStorageWritable()) {
                    // パブリックディレクトリのファイルに書き込む
                    try {
                        //Log.d("test_service", "writing: "+xyz_str);
                        outputWriter.write(xyz_str+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // ちなみにDDMSで確認したところ、確認時の環境下では
                    // "/mnt/sdcard/Download/myfile.txt"
                    // に書き込まれた。

                }
            }

            if (sum % 100 == 0) {
                long sec = sum / 100;
                Log.d("test_service", sec+" sec");
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public String shapeDateString(int date) {
        String shaped;
        if (date < 10) {
            shaped = String.format("%02d", date);
        }
        else shaped = String.valueOf(date);
        return shaped;
    }

    /**
     * 現在日時取得
     * @param underline: 1 -> アンダーバーあり， 0 -> なし
     * @return 現在日時
     */
    public String getPresentTime (int underline) {
        // TimeはAPI22以降は対応していないため，GregorianCalendarを使用
        Calendar cal = new GregorianCalendar();
        String year = shapeDateString(cal.get(Calendar.YEAR));
        String month = shapeDateString(cal.get(Calendar.MONTH)+1);
        String day = shapeDateString(cal.get(Calendar.DAY_OF_MONTH));
        String hour = shapeDateString(cal.get(Calendar.HOUR_OF_DAY));
        String minute = shapeDateString(cal.get(Calendar.MINUTE));
        String second = shapeDateString(cal.get(Calendar.SECOND));

        //Log.d("test_service", "time ok");
        if (underline == 1) {
            return year + month + day + "_" + hour + minute + second;
        }
        else {
            return year + month + day + hour + minute + second;
        }
    }
}

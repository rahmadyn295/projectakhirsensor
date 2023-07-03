package com.hp.projekakhirsensor;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SENSOR_PERMISSION_CODE = 1;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private TextView stepCountTextView;
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    private int stepCount = 0;
    private DatabaseHelper databaseHelper;
    private boolean isTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountTextView = findViewById(R.id.step_count_textview);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        resetButton = findViewById(R.id.reset_button);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        databaseHelper = new DatabaseHelper(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCount();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
        }
    }

    private void startTracking() {
        if (!isTracking) {
            isTracking = true;
            registerStepSensor();
            Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tracking already started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopTracking() {
        if (isTracking) {
            isTracking = false;
            unregisterStepSensor();
            Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tracking not started", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCount() {
        stepCount = 0;
        stepCountTextView.setText(String.valueOf(stepCount));
        Toast.makeText(this, "Count reset", Toast.LENGTH_SHORT).show();
    }

    private void registerStepSensor() {
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Step sensor not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void unregisterStepSensor() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && isTracking) {
            int steps = (int) event.values[0];
            stepCount = steps;
            stepCountTextView.setText(String.valueOf(stepCount));
            saveDataToDatabase(stepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void saveDataToDatabase(int steps) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StepEntry.COLUMN_STEP_COUNT, steps);

        db.insert(DatabaseContract.StepEntry.TABLE_NAME, null, values);
        db.close();
    }
}

package com.example.neekondev.quickscan;

import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.vision.CameraSource;

public class settings extends AppCompatActivity {

    Switch fSwitch;
    Button backBt;
    Camera cam;
    Camera.Parameters p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        backBt = (Button) findViewById(R.id.button2);
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMain);
            }
        });
        fSwitch = (Switch)findViewById(R.id.switch1);
        fSwitch.setChecked(false);
        fSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FLASH_ON();
                } else {
                    FLASH_OFF();
                }
            }
        });
    }
    public void FLASH_ON()
    {
        cam = Camera.open();
        p = cam.getParameters();
        p.setFlashMode(Camera
                .Parameters
                .FLASH_MODE_TORCH);
        cam.setParameters(p);
        cam.startPreview();
    }

    public void FLASH_OFF()
    {
        cam.stopPreview();
        cam.release();
    }

}

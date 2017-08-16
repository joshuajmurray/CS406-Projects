package com.hfad.throttlemonitor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    SeekBar durBar;
    TextView valDur;
    SeekBar delBar;
    TextView valDel;
    CheckBox clutchType;
    CheckBox brakeType;
    static String duration = "-";
    static String delay = "-";
    static String clutch = "0";
    static String brake = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        durBar = findViewById(R.id.duration);
        valDur = findViewById(R.id.valDuration);
        delBar = findViewById(R.id.delay);
        valDel = findViewById(R.id.valDelay);
        clutchType = findViewById(R.id.clutch_type);
        brakeType = findViewById(R.id.brake_type);

        durBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int durProgress, boolean fromUser) {
                duration = String.valueOf(durProgress);
                valDur.setText(duration);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        delBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int delProgress, boolean fromUser) {
                delay = String.valueOf(delProgress);
                valDel.setText(delay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        clutchType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clutch = String.valueOf(clutchType);
            }
        });

        brakeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brake = String.valueOf(brakeType);
            }
        });
    }

    public void update(View view){
//      build string to sent to micro via bluetooth
//      Send “Set”: <Command><blipMs><delayMs><clutchType><brakeType><CKSM> : <02><100><150><0><0><05>
        StringBuilder sb = new StringBuilder();
        sb.append("<02><");
        sb.append(duration);
        sb.append("><");
        sb.append(delay);
        sb.append("><");
        if(clutchType.isChecked()) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        sb.append("><");
        if(brakeType.isChecked()) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        sb.append(">");
        String s = sb.toString();
        NMEA_CKSM x = new NMEA_CKSM();
        sb.append("<");
        sb.append(x.calcCheckSum(s));
        sb.append(">");
        Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
//        Send to micro and check for reply <OK>
        BT_TX_RX y = new BT_TX_RX();
        y.send(sb.toString());
        s = y.receive();
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        if(s != "<OK>"){
            Toast.makeText(getApplicationContext(), "Settings Failed to Set!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Settings Set!", Toast.LENGTH_LONG).show();
        }
    }
}


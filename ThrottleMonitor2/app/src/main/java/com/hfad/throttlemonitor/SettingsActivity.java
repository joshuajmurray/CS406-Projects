package com.hfad.throttlemonitor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    SeekBar durBar;
    TextView valDur;
    SeekBar delBar;
    TextView valDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        durBar = (SeekBar)findViewById(R.id.duration);
        valDur = (TextView)findViewById(R.id.valDuration);
        delBar = (SeekBar)findViewById(R.id.delay);
        valDel = (TextView)findViewById(R.id.valDelay);

        durBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int durProgress, boolean fromUser) {
                valDur.setText(String.valueOf(durProgress));
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
                valDel.setText(String.valueOf(delProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}


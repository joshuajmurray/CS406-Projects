package com.hfad.throttlemonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class SettingsActivity extends Activity {

    SeekBar durBar;
    TextView valDur;
    SeekBar delBar;
    TextView valDel;
    CheckBox clutchType;
    CheckBox brakeType;
    Handler mHandler;
    ImageView clutchStatus;
    ImageView brakeStatus;
    ImageView blipStatus;


    static String duration = "-";
    static String delay = "-";
    static String clutch = "0";
    static String brake = "0";
    private TextView text;
//  commands
    private static final String GET = "01";
    private static final String SET = "02";
    private static final String STATUS = "03";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        test t = new test();

        durBar = findViewById(R.id.duration);
        valDur = findViewById(R.id.valDuration);
        delBar = findViewById(R.id.delay);
        valDel = findViewById(R.id.valDelay);
        clutchType = findViewById(R.id.clutch_type);
        brakeType = findViewById(R.id.brake_type);
        text = findViewById(R.id.statusDisplay);
        clutchStatus = findViewById(R.id.clutchStatus);
        brakeStatus = findViewById(R.id.brakeStatus);
        blipStatus = findViewById(R.id.blipStatus);

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

        NMEA_CKSM n = new NMEA_CKSM();
        StringBuilder sb = new StringBuilder();
        sb.append(GET);
        sb.append(":");//delimiter for cksm
        String[] body = sb.toString().split(":");//first element is data
        sb.append(n.calcCheckSum(body[0]));

        String getReply = t.sim(sb.toString());

        String[] received = getReply.split(":");//first element is data second is cksm
        String[] msgBody;
        if(received[0].contains(",")) {
            msgBody = received[0].split(",");//first element is command
        }
        else {
            msgBody = received;
        }

        StringBuilder reply = new StringBuilder();
        for(String val : msgBody) {
            reply.append(val);
        }

        String cksm = received[1].trim();//could error on no return!
        String calcdCksm = n.calcCheckSum(received[0]).trim();

        if(cksm.equals(calcdCksm)) {//parse reply
//            Toast.makeText(getApplicationContext(), reply.toString(), Toast.LENGTH_LONG).show();
            if("00".equals(msgBody[0].toString())) {
                text.setText("No Errors");
            } else {
                text.setText("Error found");
            }
            durBar.setProgress(Integer.parseInt(msgBody[1].toString()));
            delBar.setProgress(Integer.parseInt(msgBody[2].toString()));
            if("00".equals(msgBody[3].toString())) {
                clutchType.setChecked(false);
            } else {// if("01".equals(msgBody[3].toString())) {
                clutchType.setChecked(true);
            }
            if("00".equals(msgBody[4].toString())) {
                brakeType.setChecked(false);
            } else {//if("01".equals(msgBody[4].toString())) {
                brakeType.setChecked(true);
            }
            getStatusPeriodically();//start polling micro
        } else {
            Toast.makeText(getApplicationContext(), "CHECKSUM FAILED", Toast.LENGTH_LONG).show();
        }

        text.setText(getReply);
    }

    public void update(View view){
        test t = new test();
        NMEA_CKSM n = new NMEA_CKSM();
        StringBuilder sb = new StringBuilder();
        sb.append(SET);
        sb.append(",");
        sb.append(durBar.getProgress());//blip
        sb.append(",");
        sb.append(delBar.getProgress());//delay
        sb.append(",");
        if(clutchType.isChecked()) {//clutchType
            sb.append("01");
        } else {
            sb.append("00");
        }
        sb.append(",");
        if(brakeType.isChecked()) {//brakeType
            sb.append("01");
        } else {
            sb.append("00");
        }
        sb.append(":");//delimiter for cksm
        String[] body = sb.toString().split(":");//first element is data
        sb.append(n.calcCheckSum(body[0]));
//message built
        String getReply = t.sim(sb.toString());

        String[] received = getReply.split(":");//first element is data second is cksm
        String[] msgBody;
            if(received[0].contains(",")) {
            msgBody = received[0].split(",");//first element is command
        }
            else {
            msgBody = received;
        }

        StringBuilder reply = new StringBuilder();
            for(String val : msgBody) {
            reply.append(val);
        }

        String cksm = received[1].trim();//could error on no return!
        String calcdCksm = n.calcCheckSum(received[0]).trim();

        if(cksm.equals(calcdCksm)) {//parse reply
            Toast.makeText(getApplicationContext(), reply.toString(), Toast.LENGTH_SHORT).show();
            if("00".equals(msgBody[0].toString())) {
                text.setText("No Errors");
            } else {
                text.setText("Error found");
            }
        } else {
            Toast.makeText(getApplicationContext(), "CHECKSUM FAILED", Toast.LENGTH_LONG).show();
            text.setText("CHECKSUM FAILED");
        }
//        text.setText(getReply);
    }

    protected void getStatusPeriodically() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                String val = (String) msg.obj;
//                Toast.makeText(getApplicationContext(), val, Toast.LENGTH_SHORT).show();
            }
        };

        new Thread(new Runnable() {
            Random rand = new Random();
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                test t = new test();
                                NMEA_CKSM n = new NMEA_CKSM();
                                StringBuilder sb = new StringBuilder();
                                sb.append(STATUS);
                                sb.append(":");//delimiter for cksm
                                String[] body = sb.toString().split(":");//first element is data
                                sb.append(n.calcCheckSum(body[0]));

                                String getReply = t.sim(sb.toString());

                                String[] received = getReply.split(":");//first element is data second is cksm
                                String[] msgBody;
                                if(received[0].contains(",")) {
                                    msgBody = received[0].split(",");//first element is command
                                }
                                else {
                                    msgBody = received;
                                }

                                StringBuilder reply = new StringBuilder();
                                for(String val : msgBody) {
                                    reply.append(val);
                                }

                                String cksm = received[1].trim();//could error on no return!
                                String calcdCksm = n.calcCheckSum(received[0]).trim();

                                Message msg = new Message();
                                if(cksm.equals(calcdCksm)) {
                                    switch (msgBody[0].toString()) {
                                        case "0":
                                            clutchStatus.setImageResource(R.drawable.red_led_off);
                                            brakeStatus.setImageResource(R.drawable.red_led_off);
                                            blipStatus.setImageResource(R.drawable.red_led_off);
                                            break;
                                        case "1":
                                            clutchStatus.setImageResource(R.drawable.red_led_off);
                                            brakeStatus.setImageResource(R.drawable.red_led_off);
                                            blipStatus.setImageResource(R.drawable.red_led_on);
                                            break;
                                        case "2":
                                            clutchStatus.setImageResource(R.drawable.red_led_off);
                                            brakeStatus.setImageResource(R.drawable.red_led_on);
                                            blipStatus.setImageResource(R.drawable.red_led_off);
                                            break;
                                        case "3":
                                            clutchStatus.setImageResource(R.drawable.red_led_off);
                                            brakeStatus.setImageResource(R.drawable.red_led_on);
                                            blipStatus.setImageResource(R.drawable.red_led_on);
                                            break;
                                        case "4":
                                            clutchStatus.setImageResource(R.drawable.red_led_on);
                                            brakeStatus.setImageResource(R.drawable.red_led_off);
                                            blipStatus.setImageResource(R.drawable.red_led_off);
                                            break;
                                        case "5":
                                            clutchStatus.setImageResource(R.drawable.red_led_on);
                                            brakeStatus.setImageResource(R.drawable.red_led_off);
                                            blipStatus.setImageResource(R.drawable.red_led_on);
                                            break;
                                        case "6":
                                            clutchStatus.setImageResource(R.drawable.red_led_on);
                                            brakeStatus.setImageResource(R.drawable.red_led_on);
                                            blipStatus.setImageResource(R.drawable.red_led_off);
                                            break;
                                        case "7":
                                            clutchStatus.setImageResource(R.drawable.red_led_on);
                                            brakeStatus.setImageResource(R.drawable.red_led_on);
                                            blipStatus.setImageResource(R.drawable.red_led_on);
                                            break;
                                        default:
                                            clutchStatus.setImageResource(R.drawable.red_led_off);
                                            brakeStatus.setImageResource(R.drawable.red_led_off);
                                            blipStatus.setImageResource(R.drawable.red_led_off);
                                    }
//                                    msg.obj = msgBody[0].toString();//reply from mirco
                                } else {
//                                    msg.obj = "ERROR";//no reply from mirco
                                }

//                                mHandler.sendMessage(msg);
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    private int returnSize(String command){
        int size = 0;
        switch (command) {
            case GET:
                size = 4;
                break;
            case SET:
                size = 1;
                break;
            case STATUS:
                size = 1;
                break;
            default:
                break;
        }
        return size;
    }
}


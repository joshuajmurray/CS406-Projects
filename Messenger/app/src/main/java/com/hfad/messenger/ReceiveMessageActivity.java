package com.hfad.messenger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class ReceiveMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_message);
    }
    //call onSendMeaage() when button is clicked
    public void onSendMessage(View view){
        Intent intent = new Intent(this, ReceiveMessageActivity.class);
        startActivity(intent);
    }
}

package com.hfad.throttlemonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Connect extends Activity {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private byte[] mmBuffer; // store for the stream

    private static final int REQUEST_ENABLE_BT = 1;
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button listBtn;
    private Button setBtn;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket mmSocket;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private boolean deviceFound = false;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private String MAC;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int deviceLocation = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            text.setText("Status: not supported");

            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth", Toast.LENGTH_LONG).show();
        } else {
            text = findViewById(R.id.text);

            listBtn = findViewById(R.id.paired);
            listBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    list(v);
                }
            });

            setBtn = findViewById(R.id.settings);
            setBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    get(v);
                }
            });

            myListView = findViewById(R.id.listView1);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);

            if (!myBluetoothAdapter.isEnabled()) {
                Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

                Toast.makeText(getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_LONG).show();
            }
            // get paired devices
            pairedDevices = myBluetoothAdapter.getBondedDevices();

            // put it's one to the adapter
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().contains("ARCFLASH_DASH")) {
                    BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    MAC = device.getAddress();
                    deviceFound = true;
                    break;
                }
            }

            if (deviceFound) {
                String deviceInfo = BTArrayAdapter.getItem(deviceLocation);
                String[] parts = deviceInfo.split("\n");

                BluetoothSocket tmp = null;
                mmSocket = tmp;

                BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(MAC);

                try {// Get a BluetoothSocket to connect with the given BluetoothDevice.
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                mmSocket = tmp;
                myBluetoothAdapter.cancelDiscovery();

                try {// Connect to the remote device through the socket. This call blocks until it succeeds or throws an exception.
                    mmSocket.connect();
                } catch (IOException connectException) {// Unable to connect; close the socket and return.
                    try {
                        mmSocket.close();
                        Toast.makeText(getApplicationContext(), connectException.toString(), Toast.LENGTH_LONG).show();
                    } catch (IOException closeException) {
                        Toast.makeText(getApplicationContext(), closeException.toString(), Toast.LENGTH_LONG).show();
                    }
                    return;
                }

                Toast.makeText(getApplicationContext(), "Socket OPENED", Toast.LENGTH_LONG).show();
                text.setText("Status: Socket OPENED");

                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                try {
                    tmpIn = mmSocket.getInputStream();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                try {
                    tmpOut = mmSocket.getOutputStream();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
//                readData();
            } else {
                Toast.makeText(getApplicationContext(), "No suitable device found", Toast.LENGTH_LONG).show();
                text.setText("Status: No suitable device found");
            }
        }
    }

//    /** Called when the user taps the Settings button */
//    public void settings(View view) {
//        Intent intent = new Intent(this, SettingsActivity.class);
//        startActivity(intent);
//    }

    public void get(View view) {
        if (mmSocket.isConnected()) {
            byte[] bytes = new byte[]{0x01, 0x33, 0x0D};
            try {
                mmOutStream.write(bytes);
                Toast.makeText(getApplicationContext(), "Sending: GET", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                text.setText(e.toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_LONG).show();
        }
    }

    public void readData() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                Toast.makeText(getApplicationContext(), mmBuffer.toString(), Toast.LENGTH_LONG).show();
                // Send the obtained bytes to the UI activity.
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }

    public void list(View view) {
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for (BluetoothDevice device : pairedDevices)
            BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

        Toast.makeText(getApplicationContext(), "Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            Toast.makeText(getApplicationContext(), "Device found!",
                    Toast.LENGTH_LONG).show();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
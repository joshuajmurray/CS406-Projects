package com.hfad.throttlemonitor;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Connect extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button getBtn;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private static final int REQUEST_DISCOVERABLE_BT = 0;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket mmSocket;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    MyBluetoothService bt = new MyBluetoothService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            getBtn.setEnabled(false);
            text.setText("Status: not supported");

            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            text = findViewById(R.id.text);

            onBtn = findViewById(R.id.turnOn);
            onBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    on(v);
                }
            });

            offBtn = findViewById(R.id.turnOff);
            offBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    off(v);
                }
            });

            listBtn = findViewById(R.id.paired);
            listBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    list(v);
                }
            });

            getBtn = findViewById(R.id.settings);
            getBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    get(v);
                }
            });

            myListView = findViewById(R.id.listView1);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);

            // ListView on item selected listener.
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String deviceInfo = BTArrayAdapter.getItem(position);
                    String[] parts = deviceInfo.split("\n");
                    String NAME = parts[0];
                    String MAC = parts[1];// Get the device MAC address
                    Toast.makeText(getApplicationContext(), NAME, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), MAC, Toast.LENGTH_LONG).show();
                    BluetoothSocket tmp = null;
                    mmSocket = tmp;

                    BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(MAC);
                    // Attempt to connect to the device
                    try {// Get a BluetoothSocket to connect with the given BluetoothDevice.
                        tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                    mmSocket = tmp;
                    myBluetoothAdapter.cancelDiscovery();

                    try {
                        // Connect to the remote device through the socket. This call blocks
                        // until it succeeds or throws an exception.
                        mmSocket.connect();
                    } catch (IOException connectException) {
                        // Unable to connect; close the socket and return.
                        try {
                            mmSocket.close();
                            Toast.makeText(getApplicationContext(), connectException.toString(), Toast.LENGTH_LONG).show();
                        } catch (IOException closeException) {
                            Toast.makeText(getApplicationContext(), closeException.toString(), Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

//                    The connection attempt succeeded. Perform work associated with
//                    the connection in a separate thread.
                    Toast.makeText(getApplicationContext(), "Socket OPENED", Toast.LENGTH_LONG).show();
                    text.setText("Status: Socket OPENED");

//                    bt.ConnectedThread(mmSocket);
                    InputStream tmpIn = null;
                    OutputStream tmpOut = null;

//                     Get the input and output streams; using temp objects because
//                     member streams are final.
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
                }
            });
        }
    }

    public void get(View view){
        if (mmSocket.isConnected()) {
            byte[] bytes = new byte[]{0x01, 0x33, 0x0D};
            try {
                mmOutStream.write(bytes);
                Toast.makeText(getApplicationContext(), "Sending: GET", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                text.setText(e.toString());
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void on(View view){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
      else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }

    public void list(View view){
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
        BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

        Toast.makeText(getApplicationContext(),"Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            Toast.makeText(getApplicationContext(),"Device found!",
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
/*
    public void find(View view) {
        if (myBluetoothAdapter.isDiscovering()) {
            Toast.makeText(getApplicationContext(),"Discovering Devices Canceled",Toast.LENGTH_LONG).show();
            myBluetoothAdapter.cancelDiscovery();
        }
       else {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();
            Toast.makeText(getApplicationContext(),"Start Discovery",Toast.LENGTH_LONG).show();

            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }
*/
    public void off(View view){
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
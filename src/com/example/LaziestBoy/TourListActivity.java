package com.example.LaziestBoy;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListActivity;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;
import android.os.Message;

import java.util.List;
import java.util.Set;

public class TourListActivity extends ListActivity {
    private TextView selection;
    private static final String[] items = {"Official Campus Tour", "Sports Tour", "Dorms Tour", "Select Destination"};
//    private List<BluetoothDevice> mPaired;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int SUCCESS_CONNECT = 0;
    private static final int MESSAGE_READ = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tourlist);

//        mPaired = getIntent().getExtras().getParcelableArrayList("device.list");
        String deviceName = "LAZIESTBOY";
//
        BluetoothDevice result = null;

        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices != null) {
            for (BluetoothDevice device : devices) {
                showToast("Found Device");
//                showToast(result.getUuids()[0].getUuid().toString());
                if (deviceName.equals(device.getName())) {
                    result = device;
                    break;
                }
            }
        }
//
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what){
//                    case SUCCESS_CONNECT:
//                        showToast("Connected to device");
////                        String s = "Application Checking in!";
//                        break;
//                    case MESSAGE_READ:
//                        showToast((String)msg.obj);
//                        break;
//                }
//            }
//        };

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        selection = (TextView)findViewById(R.id.selection);

        ConnectThread cThread = new ConnectThread(result, mHandler);
        BluetoothSocket mSocket = cThread.getSocket();
        cThread.run();

        ConnectedThread connectedThread = new ConnectedThread(mSocket, mHandler);
        connectedThread.run();

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        selection = (TextView)findViewById(R.id.selection);


    }

    private final Handler mHandler = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS_CONNECT:
                    showToast("Connected to device");
//                        String s = "Application Checking in!";
                    break;
                case MESSAGE_READ:
                    showToast((String)msg.obj);
                    break;
            }
            return true;
        }
    });
    public void onListItemClick(ListView parent, View v, int position, long id)
    {
        String sel;
        selection.setText(items[position]);
        sel = items[position];
        Intent intent = new Intent(TourListActivity.this, MapTestActivity.class);
        intent.putExtra("selection", sel);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}

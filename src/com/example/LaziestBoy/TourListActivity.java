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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TourListActivity extends ListActivity {
//    private List<BluetoothDevice> mPaired;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int SUCCESS_CONNECT = 0;
    private static final int MESSAGE_READ = 1;

    private static final String[] items = {"Official Campus Tour", "Sports Tour", "Dorms Tour", "Engineering Tour", "Fountain Run", "Select Destination"};
    /**
     * Called when the activity is first created.
     */
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

        ConnectThread cThread = new ConnectThread(result);
        BluetoothSocket mSocket = cThread.getSocket();
        cThread.start();

        ConnectedThread connectedThread = new ConnectedThread(mSocket);
        connectedThread.start();

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));


    }

    private final Handler mHandler = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS_CONNECT:
                    showToast("Connected to LaziestBoy");
//                        String s = "Application Checking in!";
                    break;
                case MESSAGE_READ:
//                    showToast("Message has been read");
                    showToast((String)msg.obj);
                    break;
            }
            return true;
        }
    });
    public void onListItemClick(ListView parent, View v, int position, long id)
    {
        Intent intent = new Intent(TourListActivity.this, MapActivity.class);
        intent.putExtra("selection", position);
        finish();
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        //Set to Laziest Boy's UUID as default
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//        private final Handler mHandler;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
//            mHandler = mmHandler;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                if(mmDevice != null)
                    tmp = device.createRfcommSocketToServiceRecord(mmDevice.getUuids()[0].getUuid());
                else
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
//            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                mHandler.obtainMessage(SUCCESS_CONNECT).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
//        manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

        public BluetoothSocket getSocket()
        {
            return mmSocket;
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
//        private final Handler mHandler;


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
//            mHandler = mmHandler;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    buffer = new byte[128];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String s = new String(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, s).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    //                    buffer = new byte[1024];
                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
//                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//                    break;
                }
            }
        }


        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}

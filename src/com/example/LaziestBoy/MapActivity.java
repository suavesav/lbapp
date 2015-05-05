package com.example.LaziestBoy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.*;

public class MapActivity extends FragmentActivity {
    private GoogleMap mMap;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int SUCCESS_CONNECT = 0;
    private static final int MESSAGE_READ = 1;
    public String rec_msg = "";
    private int tourType;
    private Marker marker;
    private Location curPos;
    private final LatLng LOCATION_PURDUE = new LatLng(40.428619,-86.91378099999997);
    private final LatLng LOCATION_ENGR_FOUNTAIN = new LatLng(40.428619,-86.91378099999997);
    private final LatLng LOCATION_BRNG_FOUNTAIN = new LatLng(40.4249967110825, -86.91584587097168);
    private final LatLng LOCATION_LION_FOUNTAIN = new LatLng(40.42623814217773, -86.91389322280884);
    private final LatLng LOCATION_MEM_FOUTAIN = new LatLng(40.42525398322455, -86.91468179225922);
    private final LatLng LOCATION_ARMS = new LatLng(40.431175054211124, -86.91581904888153);
    private final LatLng LOCATION_HAMP = new LatLng(40.43002763221108, -86.915003657341);
    private final LatLng LOCATION_FRNY = new LatLng(40.429366120239564, -86.91425800323486);
    private final LatLng LOCATION_EE = new LatLng(40.42889244100294, -86.91165089607239);
    private final LatLng LOCATION_MSEE = new LatLng(40.42943145504485, -86.91238582134247);
    private final LatLng LOCATION_WANG = new LatLng(40.42967645999942, -86.9119942188263);
    private final LatLng LOCATION_PHYS = new LatLng(40.42966829318199, -86.91301345825195);
    private final LatLng LOCATION_WTHR = new LatLng(40.42617280427025, -86.91308856010437);
    private final LatLng LOCATION_BRWN = new LatLng(40.42677921928468, -86.91198348999023);
    private final LatLng LOCATION_ME = new LatLng(40.42823908485731, -86.91341042518616);
    private final LatLng LOCATION_POTR = new LatLng(40.42727128940262, -86.91199958324432);
    private final LatLng LOCATION_NUCL = new LatLng(40.42683026407007, -86.91105544567108);
    private final LatLng LOCATION_ENGR_MALL = new LatLng(40.43100355465838, -86.91630721092224);
    private final LatLng LOCATION_GRSM = new LatLng(40.426413737489504, -86.91062092781067);
    private final LatLng LOCATION_MATH = new LatLng(40.42616463702737, -86.91555619239807);
    private final LatLng LOCATION_LWSN = new LatLng(40.427299874278106, -86.9167149066925);
    private TextView dtv;
    private TextView spd;

    private final HashMap<String,LatLng> fountainRunTour = new HashMap<String, LatLng>(){{
        put("Engineering Fountain", LOCATION_ENGR_FOUNTAIN);
        put("Beering Fountain", LOCATION_BRNG_FOUNTAIN);
        put("Lion Fountain", LOCATION_LION_FOUNTAIN);
        put("Memorial Fountain", LOCATION_MEM_FOUTAIN);
    }};

    private final HashMap<String,LatLng> engineeringTour = new HashMap<String, LatLng>(){{
        put("Engineering Fountain", LOCATION_ENGR_FOUNTAIN);
        put("Armstrong Hall", LOCATION_ARMS);
        put("Hampton Hall of Civil Engineering", LOCATION_HAMP);
        put("Forney Hall of Chemical Engineering", LOCATION_FRNY);
        put("Electrical Engineering", LOCATION_EE);
        put("Materials Science and Electrical Engineering", LOCATION_MSEE);
        put("Wang Hall", LOCATION_WANG);
        put("Physics", LOCATION_PHYS);
        put("Wetherill ", LOCATION_WTHR);
        put("Brown", LOCATION_BRWN);
        put("Mechanical Engineering", LOCATION_ME);
        put("Potter Engineering Library", LOCATION_POTR);
        put("Nuclear Engineering", LOCATION_NUCL);
        put("Engineering Mall", LOCATION_ENGR_MALL);
        put("Grissom Hall", LOCATION_GRSM);
        put("Math Building", LOCATION_MATH);
        put("Lawson Hall", LOCATION_LWSN);
    }};

    private final List<Marker> tour_markers = new ArrayList<Marker>();
    private static final int OFFICIAL = 0;
    private static final int SPORTS = 1;
    private static final int RES_HALLS = 2;
    private static final int ENGINEERING = 3;
    private static final int FOUNTAIN = 4;
    DistanceThread dt = new DistanceThread();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        spd = (TextView)findViewById(R.id.speed);
        tourType = getIntent().getExtras().getInt("selection");

        String deviceName = "RNBT-64ED";
//        String deviceName = "LAZIESTBOY";

        BluetoothDevice result = null;

        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices != null) {
            for (BluetoothDevice device : devices) {
//                showToast(result.getUuids()[0].getUuid().toString());
                if (deviceName.equals(device.getName())) {
                    showToast("Found Device");
                    result = device;
                    break;
                }
            }
        }

        //Connect
        ConnectThread cThread = new ConnectThread(result);
        BluetoothSocket mSocket = cThread.getSocket();
        cThread.start();

        //Handle Connection
        ConnectedThread connectedThread = new ConnectedThread(mSocket);
        connectedThread.start();

        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
        goToPurdue();
        loadMarkers(tourType);
        dt.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) {
            return;
        }
    }

    public void goToPurdue()
    {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_PURDUE, 16);
        mMap.animateCamera(update);
    }


    public class DistanceThread extends Thread
    {
        public void run()
        {
            while(true)
            {
                try {

                    curPos = mMap.getMyLocation();
                    LatLng curLatLng = new LatLng(curPos.getLatitude(), curPos.getLongitude());
                    double dist = CalculationByDistance(curLatLng, LOCATION_ARMS);

//                    dtv.setText("Not changing");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public double CalculationByDistance(LatLng StartP, LatLng EndP) {
            int Radius=6371;//radius of earth in Km
            double lat1 = StartP.latitude;
            double lat2 = EndP.latitude;
            double lon1 = StartP.longitude;
            double lon2 = EndP.longitude;
            double dLat = Math.toRadians(lat2-lat1);
            double dLon = Math.toRadians(lon2-lon1);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLon/2) * Math.sin(dLon/2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult= Radius*c;
            double km=valueResult/1;
            DecimalFormat newFormat = new DecimalFormat("####");
            int kmInDec =  Integer.valueOf(newFormat.format(km));
            double meter=valueResult%1000;
            int  meterInDec= Integer.valueOf(newFormat.format(meter));
            Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);
            return Radius * c;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    //Message Handler that processes incoming messages
    private final Handler mHandler = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS_CONNECT:
                    showToast("Connected to LaziestBoy");
//                        String s = "Application Checking in!";
                    break;
                case MESSAGE_READ:
//                    showToast("Message has been read");
                    rec_msg += ((String)msg.obj);

                    if (((String)msg.obj).substring(msg.arg1 - 1, msg.arg1).equalsIgnoreCase("%")) {
                        spd.setText(rec_msg.substring(0));
                        rec_msg = "";
                    }
                    break;
            }
            return true;
        }
    });

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        //Set to Laziest Boy's UUID as default
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

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

    //Thread to dictate what happens to the bluetooth connection once its connected
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
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
                    buffer = new byte[16];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String s = new String(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, s).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
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

    public void loadMarkers(int tourType)
    {
        Iterator<Map.Entry<String, LatLng>> iterator;
        switch(tourType)
        {
            case(OFFICIAL):
                break;
            case(RES_HALLS):
                break;
            case(SPORTS):
                break;
            case(ENGINEERING):
                iterator = engineeringTour.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String, LatLng> entry = iterator.next();
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(entry.getValue())
                            .title(entry.getKey()));
                    tour_markers.add(marker);
                }
                break;
            case(FOUNTAIN):
                iterator = fountainRunTour.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String, LatLng> entry = iterator.next();
                    marker = mMap.addMarker(new MarkerOptions()
                                    .position(entry.getValue())
                                    .title(entry.getKey()));
                    tour_markers.add(marker);
                }
                break;
        }
    }


}

package com.example.LaziestBoy;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

import java.text.DecimalFormat;
import java.util.*;

public class MapActivity extends FragmentActivity {
    private GoogleMap mMap;
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

        dtv = (TextView)findViewById(R.id.distance);
        tourType = getIntent().getExtras().getInt("selection");

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
        // Initialize map options. For example:
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void goToPurdue()
    {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_PURDUE, 16);
        mMap.animateCamera(update);
    }

//    public void btn_prev(View v)
//    {
//        Intent intent = new Intent(MapActivity.this, TourListActivity.class);
//        finish();
//        startActivity(intent);
//    }

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
                    showToast(Double.toString(dist));
                    TextView tv = (TextView)findViewById(R.id.distance);
                    tv.setText(Double.toString(dist));
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

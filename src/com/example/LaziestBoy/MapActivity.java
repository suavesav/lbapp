package com.example.LaziestBoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity {
    private GoogleMap mMap;
    private int tourType;
    private final LatLng LOCATION_PURDUE = new LatLng(40.428619,-86.91378099999997);
    private final LatLng LOCATION_ENGR_FOUNTAIN = new LatLng(40.428619,-86.91378099999997);
    private final LatLng LOCATION_BRNG_FOUNTAIN = new LatLng(40.4249967110825, -86.91584587097168);
    private final LatLng LOCATION_LION_FOUNTAIN = new LatLng(40.42623814217773, -86.91389322280884);
    private final LatLng LOCATION_MEM_FOUTAIN = new LatLng(40.42525398322455, -86.91468179225922);
    private final List<Marker> tour_markers = new ArrayList<Marker>();
    private static final int OFFICIAL = 0;
    private static final int SPORTS = 1;
    private static final int RES_HALLS = 2;
    private static final int ENGINEERING = 3;
    private static final int FOUNTAIN = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        tourType = getIntent().getExtras().getInt("selection");

        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
        goToPurdue();
        loadMarkers(tourType);



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

    public void btn_prev(View v)
    {
        Intent intent = new Intent(MapActivity.this, TourListActivity.class);
        finish();
        startActivity(intent);
    }

    public void loadMarkers(int tourType)
    {
        switch(tourType)
        {
            case(OFFICIAL):
                break;
            case(RES_HALLS):
                break;
            case(SPORTS):
                break;
            case(ENGINEERING):
                break;
            case(FOUNTAIN):
                Marker marker;
                marker = mMap.addMarker(new MarkerOptions()
                        .position(LOCATION_ENGR_FOUNTAIN)
                        .title("Engineering Fountain")
                        .snippet("Gift from Class of '39 for their 50 yr anniversary."));
                tour_markers.add(marker);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(LOCATION_BRNG_FOUNTAIN)
                        .title("Beering Fountain")
                        .snippet("Originally located in front of Hovde Hall. Was built in the summer of '59"));
                tour_markers.add(marker);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(LOCATION_MEM_FOUTAIN)
                        .title("John Purdue Memorial Fountain")
                        .snippet("Fountain in memory of Purdue Founder"));
                tour_markers.add(marker);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(LOCATION_LION_FOUNTAIN)
                        .title("Lion Head Fountain")
                        .snippet("Donated by Class of 1903 as a drinking fountain"));
                tour_markers.add(marker);
                break;
        }
    }
}

//    public void onMapReady(GoogleMap map) {
//        LatLng sydney = new LatLng(-33.867, 151.206);
//
//        map.setMyLocationEnabled(true);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
//
//        map.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
//                .position(sydney));
//    }
package com.example.and.wifiapp;

/**
 * Created by and on 6/26/2017.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager mLocationManager;
    private boolean myflag = false;
    private  MarkerOptions a ;
    private Marker m;
    private BitmapDescriptor iconStyle;
    private String toWriteOn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle bund = getIntent().getExtras();

        if (bund.getString("point") != null) {
            toWriteOn = bund.getString("point");
        }

        iconStyle = BitmapDescriptorFactory.fromResource(R.mipmap.icon_wifi_new);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constant.LOCATION_REFRESH_TIME,
                Constant.LOCATION_REFRESH_DISTANCE, mLocationListener);


    }

    //    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        //Initialize Google Play Services
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                mMap.setMyLocationEnabled(true);
//            }
//        }
//        else {
//            mMap.setMyLocationEnabled(true);
//
//        }
//
//    }
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        // Check if we were successful in obtaining the map.

        if (mMap != null) {


            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub

                if(!myflag) {
                    double zoomLevel = 16.0; //This goes up to 21
                    LatLng MyLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, (float) zoomLevel));
                    a = new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).icon(iconStyle).title(toWriteOn);
                   // mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("it's me!!"));
                    m = mMap.addMarker(a);
                   myflag = true;
                }
                else{
                    m.setPosition(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
                }


                }

            });

        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            // Add a marker in Sydney, Australia, and move the camera.
            double zoomLevel = 16.0; //This goes up to 21
            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(me).title("Srength: 99%"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, (float) zoomLevel));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };


}

package com.example.klbc.locationtracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.MailTo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Permission;

//
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    MarkerOptions mo;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mo = new MarkerOptions().position(new LatLng(0, 0)).title("My Current Location");
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //without any criteria of accuracy and power we will use the gps provider of the location updates

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //here we need to request from the user to give us the permissions to use GPS
            return;
        }else{
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }

        //we assume that the GPS provider is Enabled so we don't need to show any alter msg to direct
        //the user to setting

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(mo);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myloc = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myloc);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }
}

package com.passageway;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerDragListener {

    private FieldUnit unit;
    //private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private PermissionListener mPermissionListener;
    private GoogleMap mGoogleMap;
    private FloatingActionButton fabLocation;
    private EditText name;
    private EditText building;
    private EditText floor;
    private EditText wing;
    private EditText mac;
    private EditText ip;
    private EditText lat;
    private EditText lon;
    private RadioGroup dirGroup;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_editor);

        name = (EditText) findViewById(R.id.input_name);
        building = (EditText) findViewById(R.id.input_building);
        floor = (EditText) findViewById(R.id.input_floor);
        wing = (EditText) findViewById(R.id.input_wing);
        mac = (EditText) findViewById(R.id.input_mac);
        ip = (EditText) findViewById(R.id.input_ip);
        lat = (EditText) findViewById(R.id.input_lat);
        lon = (EditText) findViewById(R.id.input_long);
        dirGroup = (RadioGroup) findViewById(R.id.radioGroup);

        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

        mPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                Log.d("Permission", "Permission Granted");
                fabLocation.callOnClick();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Log.d("Permission", "Permission Denied");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                Log.d("Permission", "Request Cancelled");
                token.continuePermissionRequest();
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference("units");

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        unit = (FieldUnit) bundle.getSerializable("FieldUnit");
        if (unit != null)
            setFormValues(unit);

        // Create an instance of GoogleAPIClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fab);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushDataToFirebase(unit.getKey());
                Snackbar.make(view, "Attributes saved to Firebase", Snackbar.LENGTH_LONG).show();
            }
        });

        fabLocation = (FloatingActionButton) findViewById(R.id.fab_location);

    }

    private boolean pushDataToFirebase(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        DatabaseReference fu = mDatabase.child(id);

        values.put("building", building.getText().toString());
        values.put("floor", Integer.parseInt(floor.getText().toString()));
        values.put("wing", wing.getText().toString());
        values.put("name", name.getText().toString());
        values.put("lat", Double.parseDouble(lat.getText().toString()));
        values.put("lon", Double.parseDouble(lon.getText().toString()));

        if (dirGroup.getCheckedRadioButtonId() == R.id.default_direction)
            values.put("direction", 0);
        else
            values.put("direction", 1);
        fu.updateChildren(values);
        return true;
    }

    private void setFormValues(FieldUnit unit) {

        if (unit.getDirection() == 0)
            dirGroup.check(R.id.default_direction);
        else
            dirGroup.check(R.id.alt_direction);

        name.setText(unit.getName());
        building.setText(unit.getBuilding());
        floor.setText(Integer.toString(unit.getFloor()));
        wing.setText(unit.getWing());
        mac.setText(unit.getCid());
        ip.setText(unit.getIp());
        lat.setText(Double.toString(unit.getLat()));
        lon.setText(Double.toString(unit.getLon()));
    }

    private void updateLocation(LatLng pLatLng){
        lat.setText(String.valueOf(pLatLng.latitude));
        lon.setText(String.valueOf(pLatLng.longitude));
        //zoom map and add marker
        if (mGoogleMap != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pLatLng, Float.parseFloat(getResources().getString(R.string.map_zoom))));
            mGoogleMap.clear(); //clear any existing markers
            Marker mMarker = mGoogleMap.addMarker(new MarkerOptions().position(pLatLng).title(name.getText().toString()));
            //mMarker.showInfoWindow();
            mMarker.setDraggable(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d("Permission","Lat: " + String.valueOf(location.getLatitude()));
        //Log.d("Permission","Lon: " + String.valueOf(location.getLongitude()));
        LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        updateLocation(mLatLng);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //fabLocation.setVisibility(View.VISIBLE);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Permission Not Granted. Asking User");

                    if(Dexter.isRequestOngoing()){
                        return;
                    }
                    Dexter.checkPermission(mPermissionListener, android.Manifest.permission.ACCESS_FINE_LOCATION);
                    return;
                }

                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(0);
                mLocationRequest.setFastestInterval(0);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, DetailActivity.this);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng mLatLng = new LatLng(Double.parseDouble(lat.getText().toString()), Double.parseDouble(lon.getText().toString()));
        updateLocation(mLatLng);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        updateLocation(marker.getPosition());
    }
}

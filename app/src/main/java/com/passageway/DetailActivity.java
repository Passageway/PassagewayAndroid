package com.passageway;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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

    private final int REQUEST_CHECK_SETTINGS = 9001;
    private int mLocationCount;

    private FieldUnit unit;
    //private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private PermissionListener mPermissionListener;
    private GoogleMap mGoogleMap;
    private FloatingActionButton fabLocation, fabSave;
    private EditText name, building, floor, wing, mac, ip, lat, lon;
    private ProgressBar mProgressBar;
    private RadioGroup dirGroup;
    private DatabaseReference mDatabase;
    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_editor);

        mLocationCount = 0;

        name = (EditText) findViewById(R.id.input_name);
        building = (EditText) findViewById(R.id.input_building);
        floor = (EditText) findViewById(R.id.input_floor);
        wing = (EditText) findViewById(R.id.input_wing);
        mac = (EditText) findViewById(R.id.input_mac);
        ip = (EditText) findViewById(R.id.input_ip);
        lat = (EditText) findViewById(R.id.input_lat);
        lon = (EditText) findViewById(R.id.input_long);
        mProgressBar = (ProgressBar) findViewById(R.id.location_loading);
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
                mProgressBar.setVisibility(View.INVISIBLE);
                //TODO: have option to go to settings
                if(response.isPermanentlyDenied()) {
                    Snackbar.make(findViewById(R.id.coordinator), "Location Unavailable. Please check app permissions", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.coordinator), "Location Unavailable. Permission needed for location", Snackbar.LENGTH_SHORT).show();
                }
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


        fabSave = (FloatingActionButton) findViewById(R.id.fab);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabSave.getBackgroundTintList() ==
                        ContextCompat.getColorStateList(getApplicationContext(), R.color.colorAccent)) {
                    pushDataToFirebase(unit.getCid());
                    Snackbar.make(view, "Attributes saved to Firebase", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, "Nothing to save", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        fabLocation = (FloatingActionButton) findViewById(R.id.fab_location);

        EditText [] fieldsSave = new EditText[]{name, building, floor, wing, mac, lat, lon};
        EditText [] fieldsLatLng = new EditText[]{lat, lon};
        assignTextWatchersSave(fieldsSave, fabSave);
        assignTextWatchersLocation(fieldsLatLng, fabLocation);
    }

    private void assignTextWatchersSave(EditText[] fields, final FloatingActionButton fab) {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getCurrentFocus() != null &&
                        getCurrentFocus().getClass() == AppCompatEditText.class) {
                    fab.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.cardview_light_background));
                    fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        };

        for (EditText a : fields) {
            a.addTextChangedListener(textWatcher);
        }

    }
    private void assignTextWatchersLocation(EditText[] fields, final FloatingActionButton fab) {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getCurrentFocus() != null &&
                        getCurrentFocus().getClass() == AppCompatEditText.class) {
                    fab.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.cardview_light_background));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        };

        for (EditText a : fields) {
            a.addTextChangedListener(textWatcher);
        }

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

        fabSave.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        fabSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.cardview_light_background));

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
        mac.setText(Utils.formatMAC(unit.getCid()));
        ip.setText(unit.getIp());
        lat.setText(Double.toString(unit.getLat()));
        lon.setText(Double.toString(unit.getLon()));
    }

    private void updateLocation(LatLng pLatLng){
        lat.setText(String.valueOf(pLatLng.latitude));
        lon.setText(String.valueOf(pLatLng.longitude));
        //zoom map and add marker
        if (mGoogleMap != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pLatLng, mGoogleMap.getCameraPosition().zoom));
            mGoogleMap.clear(); //clear any existing markers
            Marker mMarker = mGoogleMap.addMarker(new MarkerOptions().position(pLatLng).title(name.getText().toString()).draggable(true));
            //mMarker.showInfoWindow();
        }
    }

    private void turnOnLocation(){

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    DetailActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case RESULT_OK:
                    {
                        // All required changes were successfully made. Location enabled by user
                        fabLocation.callOnClick();
                        break;
                    }
                    case RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to. Location not enabled by user
                        Snackbar.make(findViewById(R.id.coordinator),"Location Unavailable. Please turn on device location setting", Snackbar.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d("Permission","Lat: " + String.valueOf(location.getLatitude()));
        //Log.d("Permission","Lon: " + String.valueOf(location.getLongitude()));
        Log.d("Accuracy",""+location.getAccuracy());
        mLocationCount++;
        if(location.getAccuracy() < mLocationCount*5) {
            LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            updateLocation(mLatLng);
            mProgressBar.setVisibility(View.INVISIBLE);
            mLocationCount = 0;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            fabLocation.setColorFilter(ContextCompat.getColor(this, R.color.cardview_light_background));
            fabLocation.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
            fabSave.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.cardview_light_background));
            fabSave.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorAccent));
        }
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
                mLocationRequest.setInterval(500);
                mLocationRequest.setFastestInterval(100);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, DetailActivity.this);
                turnOnLocation();
                mProgressBar.setVisibility(View.VISIBLE);
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
        mGoogleMap.setOnMarkerDragListener(this);
        LatLng mLatLng = new LatLng(Double.parseDouble(lat.getText().toString()), Double.parseDouble(lon.getText().toString()));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, Float.parseFloat(getResources().getString(R.string.map_zoom))));
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
        Log.d("Drag", "Drag End");
        updateLocation(marker.getPosition());

        fabLocation.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        fabLocation.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.cardview_light_background));

        fabSave.setColorFilter(ContextCompat.getColor(this, R.color.cardview_light_background));
        fabSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
    }
}

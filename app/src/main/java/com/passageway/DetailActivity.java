package com.passageway;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class DetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private FieldUnit unit;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    EditText name;
    EditText building;
    EditText floor;
    EditText wing;
    EditText mac;
    EditText ip;
    EditText lat;
    EditText lon;
    RadioGroup dirGroup;
    DatabaseReference mDatabase;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Attributes saved to Firebase", Snackbar.LENGTH_LONG).show();
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d("Permission", "Permission Granted");
                        mGoogleApiClient.connect();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }, android.Manifest.permission.ACCESS_FINE_LOCATION);

                pushDataToFirebase();
                Snackbar.make(view, "Attributes saved to Firebase" + unit.getKey(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void pushDataToFirebase() {
        //TODO: Fill this out
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

    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("Permission","Lat: " + String.valueOf(mLastLocation.getLatitude()));
            Log.d("Permission","Lon: " + String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

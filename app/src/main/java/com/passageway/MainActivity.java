package com.passageway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.checkPermission(new PermissionListener() {
            @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
            @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
        }, Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
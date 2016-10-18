package com.passageway;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<FieldUnit> mUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.checkPermission(new PermissionListener() {
            @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
            @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
        }, Manifest.permission.ACCESS_FINE_LOCATION);

        mAuth = FirebaseAuth.getInstance();
        mUnits = new ArrayList<>();
    }

    public void createRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        //TODO: pass in the field units
        RecyclerView.Adapter mAdapter = new RecAdapter(mUnits, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //login success
                        Log.d("auth", "signInAnonymously:onComplete:" + task.isSuccessful());
                        mDatabase = FirebaseDatabase.getInstance().getReference("units");
                        mUnits.clear();
                        // Read from the database
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value(s) and again
                                // whenever data at this location is updated.
                                Map<String, Object> units = (Map<String, Object>) dataSnapshot.getValue();
                                for (Object unit : units.values()) {
                                    Map<String, Object> attributes = (Map<String, Object>) unit;
                                    mUnits.add(new FieldUnit(attributes.get("building").toString(),attributes.get("cid").toString(),
                                            (int)(long) attributes.get("direction"),(int)(long) attributes.get("floor"),
                                            (double)(long) attributes.get("lat"), (double)(long) attributes.get("lon"),
                                            attributes.get("name").toString(), attributes.get("wing").toString()));
                                }
                                //mUnits.add(
                                Log.d("data", "Value: " + mUnits.get(0).getName() + " " + mUnits.get(0).getBuilding() + " " + mUnits.get(0).getDirection() + " " + mUnits.get(0).getLat());

                                createRecyclerView();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("data", "Failed to read value.", error.toException());
                            }
                        });

                        //login failure
                        if (!task.isSuccessful()) {
                            Log.w("auth", "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
}
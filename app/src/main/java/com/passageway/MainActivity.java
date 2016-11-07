package com.passageway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<FieldUnit> mUnits;
    private RecAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.units_loading);
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
        mAdapter = new RecAdapter(mUnits, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView,
                    new RecyclerItemClickListener.OnItemClickListener(){
                        @Override
                        public void onItemClick(View view, int position){
                            configureIntent(mAdapter.getPositionInfo(position));
                        }
                        @Override
                        public void onItemLongClick(View view, int position) {

                        }

                    }));

        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();

        signInToFirebase();
    }

    public void signInToFirebase() {
        if (Utils.isOnline(getApplicationContext())) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //login success
                            Log.d("auth", "signInAnonymously:onComplete:" + task.isSuccessful());
                            mDatabase = FirebaseDatabase.getInstance().getReference("units");

                            // Read from the database
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value(s) and again
                                    // whenever data at this location is updated.
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    mUnits.clear();

                                    Map<String, Object> units = (Map<String, Object>) dataSnapshot.getValue();
                                    Log.d("units", units.toString());
                                    for (Map.Entry<String, Object> unit : units.entrySet()) {
                                        Map<String, Object> attributes = (Map<String, Object>) unit.getValue();
                                        mUnits.add(new FieldUnit(attributes.get("building").toString(),
                                                unit.getKey(),     //cid is now going to be the key of the units
                                                attributes.get("ip").toString(),
                                                (int) (long) attributes.get("direction"),
                                                (int) (long) attributes.get("floor"),
                                                Double.parseDouble(attributes.get("lat").toString()),
                                                Double.parseDouble(attributes.get("lon").toString()),
                                                attributes.get("name").toString(),
                                                attributes.get("wing").toString()));
                                    }
                                    Log.d("data", "Value: " + mUnits.get(0).getName() + " " + mUnits.get(0).getBuilding() + " " + mUnits.get(0).getDirection() + " " + mUnits.get(0).getLat());
                                    createRecyclerView();
                                }

                                // Data listener cancelled
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
                        }
                    });
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.activity_main), "No Internet Connection", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInToFirebase();
                }
            }).show();
        }
    }

    public void configureIntent(FieldUnit unit) {
        Intent i = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("FieldUnit", unit);
        i.putExtras(bundle);
        this.startActivity(i);
    }
}
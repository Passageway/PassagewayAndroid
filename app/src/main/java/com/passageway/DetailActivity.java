package com.passageway;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity extends AppCompatActivity {

    FieldUnit unit;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

}

package com.passageway;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class DetailActivity extends AppCompatActivity {

    FieldUnit unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_editor);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        unit = (FieldUnit) bundle.getSerializable("FieldUnit");
        if (unit != null)
            setFormValues(unit);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setFormValues(FieldUnit unit) {
        EditText name = (EditText) findViewById(R.id.input_name);
        EditText building = (EditText) findViewById(R.id.input_building);
        EditText floor = (EditText) findViewById(R.id.input_floor);
        EditText wing = (EditText) findViewById(R.id.input_wing);
        EditText mac = (EditText) findViewById(R.id.input_mac);
        EditText ip = (EditText) findViewById(R.id.input_ip);
        EditText lat = (EditText) findViewById(R.id.input_lat);
        EditText lon = (EditText) findViewById(R.id.input_long);

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

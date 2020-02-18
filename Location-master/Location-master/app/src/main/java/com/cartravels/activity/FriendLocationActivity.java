package com.cartravels.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.cartravels.loca.R;

import java.util.Objects;

public class FriendLocationActivity extends AppCompatActivity implements OnMapReadyCallback, ValueEventListener {

    private String latitude;
    private String longitude;
    private String name;
    private String lastLocationUpdateTime;

    private TextView mTextViewLastLocation;
    private TextView mTextViewLastLocationUpdateTime;

    private DatabaseReference mFriendReference;

    private GoogleMap map;
    private float mapZoomLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_location);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent mIntent = getIntent();
        String mobileNo = mIntent.getStringExtra("mobile_no");
        name = mIntent.getStringExtra("name");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
        mFriendReference = mDatabaseReference.child("users").child(mobileNo);

        TextView mTextViewName = (TextView) findViewById(R.id.name);
        mTextViewLastLocation = (TextView) findViewById(R.id.last_location);
        mTextViewLastLocationUpdateTime = (TextView) findViewById(R.id.last_location_update_time);

        Button mButtuonLocation = (Button) findViewById(R.id.button_location);
        mButtuonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationOnMap();
            }
        });

        mTextViewName.setText(name);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
            fragment.getMapAsync(this);
        }

        mapZoomLevel = 13;

        mDatabaseReference.child("users").child(mobileNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        requestLocationData();

        showLocationOnMap();
    }

    private void requestLocationData() {
        mFriendReference.child("latitude").addValueEventListener(this);
        mFriendReference.child("longitude").addValueEventListener(this);
        mFriendReference.child("last_location_update_time").addValueEventListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void showLocationOnMap() {
        if (map != null && latitude != null && longitude != null) {

            mTextViewLastLocation.setText(latitude + ", " + longitude);

            LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, mapZoomLevel));

            map.clear();
            map.addMarker(new MarkerOptions()
                    .title(name + "'s location")
                    .snippet("at " + lastLocationUpdateTime)
                    .position(latlng)).showInfoWindow();
        } else {
            requestLocationData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLocationOnMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mapZoomLevel = 13;

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mapZoomLevel = map.getCameraPosition().zoom;
            }
        });

        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);

        showLocationOnMap();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            switch (Objects.requireNonNull(dataSnapshot.getKey())) {
                case "latitude":
                    latitude = dataSnapshot.getValue().toString();
                    break;
                case "longitude":
                    longitude = dataSnapshot.getValue().toString();
                    break;
                case "last_location_update_time":
                    lastLocationUpdateTime = dataSnapshot.getValue().toString();
                    mTextViewLastLocationUpdateTime.setText(lastLocationUpdateTime);
                    break;
            }
        }
        showLocationOnMap();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}

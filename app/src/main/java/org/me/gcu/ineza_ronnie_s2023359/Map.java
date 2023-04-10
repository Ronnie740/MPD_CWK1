package org.me.gcu.ineza_ronnie_s2023359;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private ArrayList<Earthquake> earthquakes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        earthquakes = (ArrayList<Earthquake>) intent.getSerializableExtra("earthquakes");
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        for(Earthquake earthquake : earthquakes){
            LatLng positions = new LatLng(earthquake.getLatitude(),earthquake.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(positions).title("Earthquake in "+ earthquake.getLocation()).snippet("Magnitude: " + earthquake.getMagnitude()+ "\nLocation: " + earthquake.getLocation() + "\nDate: " + earthquake.getDate()));
        }
        // Enable the zoom controls in the map
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Set up a listener for marker clicks
        googleMap.setOnMarkerClickListener(marker -> {
            // Display an alert dialog when the marker is clicked
            new AlertDialog.Builder(Map.this)
                    .setTitle(marker.getTitle())
                    .setMessage(marker.getSnippet())
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        });
        googleMap.getUiSettings().setCompassEnabled(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // Create an intent to start the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        // Set the flag to clear the activity stack and start a new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start the activity
        startActivity(intent);
        finish();

    }

}

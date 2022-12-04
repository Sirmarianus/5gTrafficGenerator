package mk.trafficgenerator5g;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        double latitude = 50.06;
        double longtitude = 19.94;

        String url = String.valueOf(this.getIntent().getData());
        if (url.contains("www.google.com/maps/@")) {
            String latLonZoom = url.substring(url.lastIndexOf("maps/@")+6);
            String [] llz = latLonZoom.split(",");
            latitude = Double.parseDouble(llz[0]);
            longtitude = Double.parseDouble(llz[1]);
        }
        Log.d("MapsActivity", "lat: " + latitude);
        Log.d("MapsActivity", "lon: " + longtitude);
        LatLng sydney = new LatLng(latitude, longtitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
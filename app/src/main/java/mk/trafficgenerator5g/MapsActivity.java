package mk.trafficgenerator5g;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import mk.trafficgenerator5g.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        double latitude = 50.06;
        double longtitude = 19.94;

        String url = String.valueOf(this.getIntent().getData());
        if (url.contains("www.google.com/maps/@")) {
            String latLonZoom = url.substring(url.lastIndexOf("maps/@")+6);
            String [] llz = latLonZoom.split(",");
            latitude = Double.parseDouble(llz[0]);
            longtitude = Double.parseDouble(llz[1]);
            Log.d("DUPA", "lat: " + latitude);
            Log.d("DUPA", "lon: " + longtitude);
        }
        LatLng sydney = new LatLng(latitude, longtitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
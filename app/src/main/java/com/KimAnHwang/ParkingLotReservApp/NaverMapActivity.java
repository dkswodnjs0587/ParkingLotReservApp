package com.KimAnHwang.ParkingLotReservApp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class NaverMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private MapView mapView;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    private FusedLocationProviderClient fusedLocationClient;

    private Marker myLocationMarker;
    private InfoWindow infoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.getUiSettings().setZoomControlEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null
                                && !Double.isNaN(location.getLatitude())
                                && !Double.isNaN(location.getLongitude())) {
                            LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            naverMap.moveCamera(CameraUpdate.scrollTo(lastLatLng));
                            showMyLocationMarker(lastLatLng);
                        } else {
                            Toast.makeText(this, "마지막 위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

            naverMap.addOnLocationChangeListener(location -> {
                if (location != null
                        && !Double.isNaN(location.getLatitude())
                        && !Double.isNaN(location.getLongitude())) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    naverMap.moveCamera(CameraUpdate.scrollTo(currentLatLng));
                    showMyLocationMarker(currentLatLng);
                }
            });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void showMyLocationMarker(LatLng position) {
        if (position == null
                || Double.isNaN(position.latitude)
                || Double.isNaN(position.longitude)) {
            // 위치 정보가 유효하지 않으면 마커 숨김 처리
            if (myLocationMarker != null) {
                myLocationMarker.setMap(null);
            }
            return;
        }

        if (myLocationMarker == null) {
            myLocationMarker = new Marker();
        }
        myLocationMarker.setPosition(position);

        if (myLocationMarker.getMap() == null) {
            myLocationMarker.setMap(naverMap);
        }

        if (infoWindow == null) {
            infoWindow = new InfoWindow();
            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    return "내 위치";
                }
            });
            infoWindow.open(myLocationMarker);
        } else {
            infoWindow.open(myLocationMarker);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (locationSource.isActivated()) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            } else {
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 생명주기 메서드들
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { mapView.onPause(); super.onPause(); }
    @Override protected void onStop() { mapView.onStop(); super.onStop(); }
    @Override protected void onDestroy() { mapView.onDestroy(); super.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
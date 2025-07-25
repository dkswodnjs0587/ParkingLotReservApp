package com.KimAnHwang.ParkingLotReservApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button loginLogoutButton;
    Button buttonMap;
    Button naverMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 버튼만 있는 레이아웃이어야 함

        auth = FirebaseAuth.getInstance();
        loginLogoutButton = findViewById(R.id.buttonLoginLogout);

        updateButton();

        loginLogoutButton.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                // 로그아웃
                auth.signOut();
                Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                updateButton();
            } else {
                // 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonMap = findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        naverMap = findViewById(R.id.naverMap);
        naverMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NaverMapActivity.class);
            startActivity(intent);
        });
    }

    private void updateButton() {
        if (auth.getCurrentUser() != null) {
            loginLogoutButton.setText("로그아웃");
        } else {
            loginLogoutButton.setText("로그인");
        }
    }
}

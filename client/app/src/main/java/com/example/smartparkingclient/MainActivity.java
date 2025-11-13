package com.example.smartparkingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnEnter;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnter = findViewById(R.id.btnEnter);
        btnExit = findViewById(R.id.btnExit);

        // Заехать
        btnEnter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
            intent.putExtra("mode", "enter");   // режим заезда
            startActivity(intent);
        });

        // Выехать
        btnExit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
            intent.putExtra("mode", "exit");    // режим выезда (пока логика та же)
            startActivity(intent);
        });
    }
}

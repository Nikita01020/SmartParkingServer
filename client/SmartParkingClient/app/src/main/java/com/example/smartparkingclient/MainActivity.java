package com.example.smartparkingclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEnter = findViewById(R.id.btnEnter);
        Button btnExit = findViewById(R.id.btnExit);

        btnEnter.setOnClickListener(v ->
                Toast.makeText(this, "Автомобиль заехал на парковку", Toast.LENGTH_SHORT).show()
        );

        btnExit.setOnClickListener(v ->
                Toast.makeText(this, "Автомобиль покинул парковку", Toast.LENGTH_SHORT).show()
        );
    }
}

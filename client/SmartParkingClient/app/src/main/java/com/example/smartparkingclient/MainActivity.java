package com.example.smartparkingclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private View statusIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheck = findViewById(R.id.btnCheck);
        tvStatus = findViewById(R.id.tvStatus);
        statusIndicator = findViewById(R.id.statusIndicator);

        btnCheck.setOnClickListener(v -> fetchStatus());
    }

    private void fetchStatus() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8000/";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> tvStatus.setText("Ошибка подключения"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String data = response.body().string();
                    try {
                        JSONObject json = new JSONObject(data);
                        final boolean occupied = json.getBoolean("occupied");
                        runOnUiThread(() -> updateUI(occupied));
                    } catch (Exception e) {
                        runOnUiThread(() -> tvStatus.setText("Ошибка данных сервера"));
                    }
                }
            }
        });
    }

    private void updateUI(boolean occupied) {
        if (occupied) {
            tvStatus.setText("Место занято 🚗");
            statusIndicator.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvStatus.setText("Место свободно ✅");
            statusIndicator.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
}

package com.example.smartparkingclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private LinearLayout parkingList;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parkingList = findViewById(R.id.parkingList);
        loadParkingPlaces();
    }

    private void loadParkingPlaces() {
        // ⚠️ IP должен совпадать с адресом Flask-сервера
        String url = "http://10.0.2.2:8000/places";


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonData);
                        JSONArray places = json.getJSONArray("places");

                        runOnUiThread(() -> {
                            parkingList.removeAllViews();
                            for (int i = 0; i < places.length(); i++) {
                                JSONObject place = places.optJSONObject(i);
                                int id = place.optInt("id");
                                String status = place.optString("status");

                                TextView tv = new TextView(MainActivity.this);
                                tv.setText("Место " + id + ": " + (status.equals("free") ? "Свободно" : "Занято"));
                                tv.setTextSize(18);
                                tv.setPadding(20, 20, 20, 20);
                                tv.setBackgroundColor(
                                        status.equals("free") ? 0xFFA8E6CF : 0xFFFF8C8C
                                );
                                parkingList.addView(tv);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

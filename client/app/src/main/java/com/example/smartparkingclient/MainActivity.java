package com.example.smartparkingclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private LinearLayout parkingList;
    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://10.0.2.2:8000"; // ⚠️ Для эмулятора Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parkingList = findViewById(R.id.parkingList);
        loadParkingPlaces();
    }

    // ================= Загрузка списка =================
    private void loadParkingPlaces() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/places")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Ошибка соединения с сервером: " + e.getMessage(), Toast.LENGTH_LONG).show()
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
                                tv.setBackgroundColor(status.equals("free") ? 0xFFA8E6CF : 0xFFFF8C8C);

                                // Добавляем обработчик клика
                                tv.setOnClickListener(v -> togglePlaceStatus(id, status));

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

    // ================= Обновление статуса =================
    private void togglePlaceStatus(int id, String currentStatus) {
        String newStatus = currentStatus.equals("free") ? "busy" : "free";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("status", newStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/update")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Ошибка при обновлении: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Статус изменён!", Toast.LENGTH_SHORT).show();
                        loadParkingPlaces(); // 🔄 обновить список
                    } else {
                        Toast.makeText(MainActivity.this, "Ошибка обновления на сервере", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

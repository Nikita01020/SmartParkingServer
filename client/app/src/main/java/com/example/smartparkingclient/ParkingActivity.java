package com.example.smartparkingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParkingActivity extends AppCompatActivity {

    private LinearLayout leftColumn;
    private LinearLayout rightColumn;
    private TextView titleText;

    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://10.0.2.2:8000";  // как у тебя
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private String mode = "enter";   // enter / exit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        leftColumn = findViewById(R.id.leftColumn);
        rightColumn = findViewById(R.id.rightColumn);
        titleText = findViewById(R.id.titleText);

        // получаем режим из Intent
        String fromIntent = getIntent().getStringExtra("mode");
        if (fromIntent != null) {
            mode = fromIntent;
        }

        if ("exit".equals(mode)) {
            titleText.setText("Выберите место для выезда");
        } else {
            titleText.setText("Выберите место для заезда");
        }

        loadParkingPlaces();
    }

    // =========== загрузка списка мест ===========
    private void loadParkingPlaces() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/places")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(ParkingActivity.this,
                                "Ошибка соединения: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    runOnUiThread(() ->
                            Toast.makeText(ParkingActivity.this,
                                    "Ошибка сервера: " + response.code(),
                                    Toast.LENGTH_LONG).show()
                    );
                    return;
                }

                String jsonData = response.body().string();

                try {
                    JSONObject json = new JSONObject(jsonData);
                    JSONArray places = json.getJSONArray("places");

                    runOnUiThread(() -> {
                        leftColumn.removeAllViews();
                        rightColumn.removeAllViews();

                        for (int i = 0; i < places.length(); i++) {
                            JSONObject place = places.optJSONObject(i);
                            if (place == null) continue;

                            int id = place.optInt("id", -1);
                            String status = place.optString("status", "unknown");

                            addSlotView(i, id, status);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(ParkingActivity.this,
                                    "Ошибка парсинга данных",
                                    Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    // =========== добавляем одно парковочное место ===========
    private void addSlotView(int index, int id, String status) {
        boolean isFree = "free".equalsIgnoreCase(status);

        // куда кладём: чётные слева, нечётные справа
        LinearLayout column = (index % 2 == 0) ? leftColumn : rightColumn;

        TextView slot = new TextView(this);
        slot.setText("Место " + id);
        slot.setTextSize(16);
        slot.setGravity(android.view.Gravity.CENTER);

        int padding = dpToPx(8);
        slot.setPadding(padding, padding, padding, padding);

        // высота и отступы
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(56)
        );
        params.setMargins(0, 0, 0, dpToPx(8));
        slot.setLayoutParams(params);

        // цвет по статусу
        int bgColor = isFree ? 0xFFA8E6CF : 0xFFFF8C8C;
        slot.setBackgroundColor(bgColor);

        // клики
        String statusForClick = status;
        slot.setOnClickListener(v -> {
            if ("enter".equals(mode)) {
                // режим заезда
                if (isFree) {
                    Toast.makeText(this,
                            "Вы выбрали место " + id,
                            Toast.LENGTH_SHORT).show();
                    togglePlaceStatus(id, statusForClick);
                } else {
                    Toast.makeText(this,
                            "Место " + id + " занято",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // режим выезда — можно, например, разрешить нажимать только занятые
                if (!isFree) {
                    Toast.makeText(this,
                            "Вы освобождаете место " + id,
                            Toast.LENGTH_SHORT).show();
                    togglePlaceStatus(id, statusForClick);
                } else {
                    Toast.makeText(this,
                            "Место " + id + " уже свободно",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        column.addView(slot);
    }

    // =========== запрос на смену статуса ===========
    private void togglePlaceStatus(int id, String currentStatus) {
        String newStatus = "free".equalsIgnoreCase(currentStatus) ? "busy" : "free";

        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("status", newStatus);
        } catch (JSONException e) {
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
                        Toast.makeText(ParkingActivity.this,
                                "Ошибка при обновлении: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(ParkingActivity.this,
                                "Статус изменён",
                                Toast.LENGTH_SHORT).show();
                        loadParkingPlaces();   // обновить схему
                    } else {
                        Toast.makeText(ParkingActivity.this,
                                "Ошибка обновления: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // =========== утилита: dp -> px ===========
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

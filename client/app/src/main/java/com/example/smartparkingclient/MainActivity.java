package com.example.smartparkingclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private Button btnEnter;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----- Drawer / Toolbar -----
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavItemSelected);

        // ----- Кнопки главного меню -----
        btnEnter = findViewById(R.id.btnEnter);
        btnExit = findViewById(R.id.btnExit);

        btnEnter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
            intent.putExtra("mode", "enter");
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
            intent.putExtra("mode", "exit");
            startActivity(intent);
        });

        // ----- Кнопка "Назад" с учётом открытого меню -----
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // если меню открыто — закрываем
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // если меню закрыто — обычное поведение "назад"
                    setEnabled(false); // отключаем этот callback, чтобы не поймать рекурсию
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    // ----- обработка пунктов бокового меню -----
    private boolean onNavItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Toast.makeText(this, "Авторизация (пока заглушка)", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_balance) {
            Toast.makeText(this, "Баланс: пока 0 ₽", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_info) {
            Toast.makeText(this, "О приложении", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

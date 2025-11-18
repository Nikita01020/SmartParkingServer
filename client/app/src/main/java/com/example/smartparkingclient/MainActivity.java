package com.example.smartparkingclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private Button btnEnter;
    private Button btnExit;

    // ----- простое состояние авторизации -----
    private boolean isAuthorized = false;
    private String currentUserName = "Гость";
    private int currentBalance = 0;

    // ссылки на элементы шапки меню
    private View headerView;
    private android.widget.TextView headerUserName;
    private android.widget.TextView headerBalance;

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

        // ----- шапка меню -----
        headerView = navigationView.getHeaderView(0);
        headerUserName = headerView.findViewById(R.id.headerUserName);
        headerBalance = headerView.findViewById(R.id.headerBalance);
        updateHeader(); // выставляем "Гость" и 0 ₽

        // ----- Кнопки главного меню -----
        btnEnter = findViewById(R.id.btnEnter);
        btnExit = findViewById(R.id.btnExit);

        btnEnter.setOnClickListener(v -> {
            if (!isAuthorized) {
                showNeedAuthDialog();
                return;
            }
            Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
            intent.putExtra("mode", "enter");
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> {
            if (!isAuthorized) {
                showNeedAuthDialog();
                return;
            }
            Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
            intent.putExtra("mode", "exit");
            startActivity(intent);
        });

        // ----- Кнопка "Назад" с учётом открытого меню -----
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    // ---------- обработка пунктов бокового меню ----------
    private boolean onNavItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            showLoginDialog();
        } else if (id == R.id.nav_balance) {
            if (!isAuthorized) {
                showNeedAuthToast();
            } else {
                Toast.makeText(this,
                        "Ваш баланс: " + currentBalance + " ₽",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_info) {
            showAboutDialog();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this,
                    "Настройки пока не реализованы",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            if (isAuthorized) {
                isAuthorized = false;
                currentUserName = "Гость";
                currentBalance = 0;
                updateHeader();
                Toast.makeText(this,
                        "Вы вышли из аккаунта",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Вы не авторизованы",
                        Toast.LENGTH_SHORT).show();
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // ---------- диалог "нужно авторизоваться" ----------
    private void showNeedAuthDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Требуется авторизация")
                .setMessage("Перед тем, как заехать или выехать, необходимо авторизоваться.")
                .setPositiveButton("Авторизоваться", (dialog, which) -> showLoginDialog())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showNeedAuthToast() {
        Toast.makeText(this,
                "Сначала авторизуйтесь",
                Toast.LENGTH_SHORT).show();
    }

    // ---------- простой диалог авторизации (заглушка) ----------
    private void showLoginDialog() {
        // Простейшая форма — только ввод имени
        final EditText inputName = new EditText(this);
        inputName.setHint("Введите имя");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(20);
        container.setPadding(padding, padding, padding, padding);
        container.addView(inputName,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        new AlertDialog.Builder(this)
                .setTitle("Авторизация")
                .setView(container)
                .setPositiveButton("Войти", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this,
                                "Имя не может быть пустым",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // тут могла бы быть реальная проверка логина/пароля
                    isAuthorized = true;
                    currentUserName = name;
                    currentBalance = 150; // фейковый баланс, для примера
                    updateHeader();

                    Toast.makeText(this,
                            "Добро пожаловать, " + currentUserName + "!",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    // ---------- "О приложении" ----------
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("О приложении")
                .setMessage("Smart Parking\n\nДемонстрационное приложение для управления парковкой.")
                .setPositiveButton("OK", null)
                .show();
    }

    // ---------- обновление шапки меню ----------
    private void updateHeader() {
        if (headerUserName != null) {
            headerUserName.setText(currentUserName);
        }
        if (headerBalance != null) {
            headerBalance.setText("Баланс: " + currentBalance + " ₽");
        }
    }

    // ---------- утилита dp -> px ----------
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

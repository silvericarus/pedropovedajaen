package com.silvericarus.parroquiasanpedropovedajaen.activities;


import android.os.Bundle;
import android.view.MenuItem;

import com.silvericarus.parroquiasanpedropovedajaen.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //Desactivar Modo Noche
        AppCompatDelegate
                .setDefaultNightMode(
                        AppCompatDelegate
                                .MODE_NIGHT_NO);

        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Información sobre la App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.finish();
        return true;
    }
}
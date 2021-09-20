package com.silvericarus.parroquiasanpedropovedajaen.activities;

import android.os.Bundle;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

    }

}

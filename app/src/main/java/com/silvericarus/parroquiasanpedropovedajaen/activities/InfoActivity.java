package com.silvericarus.parroquiasanpedropovedajaen.activities;


import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.databinding.ActivityInfoBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InfoActivity extends AppCompatActivity {

    private TextView hecho_por,texto_bugs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setupToolbar();
        hecho_por = findViewById(R.id.hechoPor);
        texto_bugs = findViewById(R.id.texto_bugs);
        hecho_por.setText(R.string.hecho_por);
        texto_bugs.setText(R.string.texto_bugs);
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
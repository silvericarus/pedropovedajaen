package com.silvericarus.parroquiasanpedropovedajaen.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;

import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.databinding.ActivityInfoBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InfoActivity extends AppCompatActivity {

    private TextView texto_bugs,contactoText,contactoTextTitle;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setupToolbar();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        texto_bugs = findViewById(R.id.texto_bugs);
        contactoText = findViewById(R.id.contactoText);
        contactoTextTitle = findViewById(R.id.contactoTextTitle);
        texto_bugs.setText(R.string.texto_bugs);
        contactoText.setText(R.string.contacto);
        int tamanioElegido = prefs.getInt(getResources().getString(R.string.pref_numArticulos_titulo),20);
        if (tamanioElegido >= 20) {
            texto_bugs.setTextSize(tamanioElegido);
            contactoText.setTextSize(tamanioElegido);
            contactoTextTitle.setTextSize(tamanioElegido);
        } else {
            texto_bugs.setTextSize(20);
            contactoText.setTextSize(20);
            contactoTextTitle.setTextSize(20);
        }
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
package com.silvericarus.parroquiasanpedropovedajaen.activities;


import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.silvericarus.parroquiasanpedropovedajaen.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InfoActivity extends AppCompatActivity {

    private TextView texto_bugs,contactoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setupToolbar();
        texto_bugs = findViewById(R.id.texto_bugs);
        contactoText = findViewById(R.id.contactoText);
        contactoText.setMovementMethod(LinkMovementMethod.getInstance());
        texto_bugs.setText(R.string.texto_bugs);
        contactoText.setText(R.string.contacto);
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
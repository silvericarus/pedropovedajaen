package com.silvericarus.parroquiasanpedropovedajaen.activities;


import android.os.Bundle;

import com.silvericarus.parroquiasanpedropovedajaen.R;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings,null);
    }
}

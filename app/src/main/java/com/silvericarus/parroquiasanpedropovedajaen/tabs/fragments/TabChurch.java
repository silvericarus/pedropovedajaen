package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silvericarus.parroquiasanpedropovedajaen.R;


public class TabChurch extends Fragment {

    public TabChurch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static TabChurch newInstance(){
        TabChurch fragment = new TabChurch();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_church, container, false);
    }
}
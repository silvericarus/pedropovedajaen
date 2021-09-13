package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.silvericarus.parroquiasanpedropovedajaen.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TabChurch extends Fragment {
    Context context;
    ImageButton btnDnJulio, btnFacebook, btnYoutube;

    public TabChurch() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    public static TabChurch newInstance(){
        TabChurch fragment = new TabChurch();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFull = inflater.inflate(R.layout.fragment_tab_church, container, false);
        btnDnJulio = viewFull.findViewById(R.id.btnDnJulio);
        btnFacebook = viewFull.findViewById(R.id.btnFacebook);
        btnYoutube = viewFull.findViewById(R.id.btnYoutube);

        btnDnJulio.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=34661941531"));
            startActivity(browserIntent);
        });

        btnFacebook.setOnClickListener(view -> {
            Intent intent=null;
            String url = "fb://page/150237331246";
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setPackage("com.facebook.katana");
                browserIntent.setData(Uri.parse(url));
                startActivity(browserIntent);
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        btnYoutube.setOnClickListener(view -> {
            Intent intent=null;
            String url = "https://www.youtube.com/c/ParroquiadeSanPedroPovedadeJa%C3%A9n/featured";
            try {
                intent =new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.youtube");
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        return viewFull;
    }

    public class DownloadTimetable extends AsyncTask<String, Void, Void> implements Response.Listener<String>,Response.ErrorListener{

        @Override
        protected Void doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (sharedPreferences.getString("lastTimetableDate", "30/12/1996").equals("30/12/1996")){
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                String currentDate = sdf.format(calendar.getTime());
                editor.putString("lastTimetableDate",currentDate);
            }
            return null;
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }

        @Override
        public void onResponse(String response) {

        }
    }
}
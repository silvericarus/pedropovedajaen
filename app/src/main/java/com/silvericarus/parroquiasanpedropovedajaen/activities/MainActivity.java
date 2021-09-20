package com.silvericarus.parroquiasanpedropovedajaen.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public TabLayout tabLayout;
    public ViewPager2 viewPager;
    public Toolbar toolbar;

    //Se ejecuta al crear el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    //Maneja cada opción del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Maneja la opción de más info
        if (id == R.id.info) {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        }else if (id == R.id.ajustes) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeSettings();
        setupToolbar();
        setupViewPager();
        setupTabLayout();
    }

    private void initializeSettings() {
        PreferenceManager.setDefaultValues(this,R.xml.settings,false);
    }


    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.view_pager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        viewPager.addItemDecoration(dividerItemDecoration);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle()));
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }

    private void setupTabLayout() {
        tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(ViewPagerAdapter.Tab.byPosition(position).title))
                .attach();

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_dark));
        tabLayout.setBackgroundColor(getResources().getColor(R.color.blue_light));
        tabLayout.setTabRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.blue_dark)));
        tabLayout.setTabTextColors(Color.parseColor("#727272"), getResources().getColor(R.color.black));
    }
}
package com.silvericarus.parroquiasanpedropovedajaen;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Desactivar Modo Noche
        AppCompatDelegate
                .setDefaultNightMode(
                        AppCompatDelegate
                                .MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);
        setupToolbar();
        setupViewPager();
        setupTabLayout();

        /*
        FloatingActionButton fab = binding.fab;



        //fab click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.view_pager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        viewPager.addItemDecoration(dividerItemDecoration);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle()));
        appBarLayout = findViewById(R.id.appBar);
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
                (tab, position) -> {
                    tab.setText(ViewPagerAdapter.Tab.byPosition(position).title);
                })
                .attach();

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_dark));
        tabLayout.setTabTextColors(Color.parseColor("#727272"), getResources().getColor(R.color.black));
        /*tabLayout.getTabAt(ViewPagerAdapter.Tab.NEWS.position)
                .getOrCreateBadge()
                .setVisible(true);//Esto pone un puntito encima
        tabLayout.getTabAt(ViewPagerAdapter.Tab.CHURCH.position)
                .getOrCreateBadge()
                .setNumber(20);//Esto pone un numero de cosas nuevas*/
    }
}
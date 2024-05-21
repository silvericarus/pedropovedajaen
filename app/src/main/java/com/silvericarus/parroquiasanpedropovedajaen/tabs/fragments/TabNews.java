package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.ImportantNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.LastNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.databinding.FragmentTabNewsBinding;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.NewsViewModel;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.CustomGridLayoutManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabNews extends Fragment {

    ImportantNewsAdapter mINAdapter;
    LastNewsAdapter mLNAdapter;
    RecyclerView mImportantNewsList;
    RecyclerView mLastNewsList;
    Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsViewModel newsViewModel;
    ProgressBar mainProgressBar;
    public ArrayList<News> importantNewsArrayList = new ArrayList<>();
    public ArrayList<News> lastNewsArrayList = new ArrayList<>();
    FragmentTabNewsBinding binding;
    TextView lastNewsTitle;
    TextView importantNewsTitle;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        newsViewModel.getImportantNewsList().observe(getViewLifecycleOwner(), news -> {
            mINAdapter.setItemList((ArrayList<News>) news);
            mINAdapter.notifyDataSetChanged();
        });

        newsViewModel.getLastNewsList().observe(getViewLifecycleOwner(), news -> {
            mLNAdapter.setItemList((ArrayList<News>) news);
            mLNAdapter.notifyDataSetChanged();
        });

        newsViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                mainProgressBar.setVisibility(View.VISIBLE);
                mLastNewsList.setVisibility(View.GONE);
                mImportantNewsList.setVisibility(View.GONE);
                lastNewsTitle.setVisibility(View.GONE);
                importantNewsTitle.setVisibility(View.GONE);
            } else {
                mainProgressBar.setVisibility(View.GONE);
                mImportantNewsList.setVisibility(View.VISIBLE);
                mLastNewsList.setVisibility(View.VISIBLE);
                lastNewsTitle.setVisibility(View.VISIBLE);
                importantNewsTitle.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> newsViewModel.fetchMainNews());

        newsViewModel.fetchMainNews();
    }

    public TabNews() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

    }

    public static TabNews newInstance() {return new TabNews();}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTabNewsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        swipeRefreshLayout = binding.swipeRefreshNews;
        mImportantNewsList = binding.listaNoticiaImportante;
        mLastNewsList = binding.listaUltimasNoticias;
        mainProgressBar = binding.mainProgressBar;
        lastNewsTitle = binding.textView;
        importantNewsTitle = binding.textView2;


        swipeRefreshLayout.setColorSchemeResources(R.color.blue_lighter);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.blue_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> newsViewModel.fetchMainNews());

        mImportantNewsList.setLayoutManager(new LinearLayoutManager(context));
        mImportantNewsList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(context);
        layoutManager.setScrollEnabled(false);
        mLastNewsList.setLayoutManager(layoutManager);
        mLastNewsList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

        mINAdapter = new ImportantNewsAdapter(importantNewsArrayList);
        mLNAdapter = new LastNewsAdapter(lastNewsArrayList);
        mImportantNewsList.setAdapter(mINAdapter);
        mLastNewsList.setAdapter(mLNAdapter);

        mINAdapter.setOnClickListener(view1 -> {
            String url;
            final News newsSelected = mINAdapter.getItemList().get(mImportantNewsList.getChildAdapterPosition(view1));
            if (!newsSelected.getUrl().startsWith("http://") && !newsSelected.getUrl().startsWith("https://"))
                url = "http://" + newsSelected.getUrl();
            else
                url = newsSelected.getUrl();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

        });
        mLNAdapter.setOnClickListener(view1 -> {
            String url;
            final News newsSelected = mLNAdapter.getItemList().get(mLastNewsList.getChildAdapterPosition(view1));
            if (!newsSelected.getUrl().startsWith("http://") && !newsSelected.getUrl().startsWith("https://"))
                url = "http://" + newsSelected.getUrl();
            else
                url = newsSelected.getUrl();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

        });

        mainProgressBar.setVisibility(View.VISIBLE);
        // Inflate the layout for this fragment
        return view;
    }
}


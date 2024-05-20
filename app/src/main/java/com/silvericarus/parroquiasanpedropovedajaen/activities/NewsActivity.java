package com.silvericarus.parroquiasanpedropovedajaen.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.LastNewsAdapter;
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

public class NewsActivity extends AppCompatActivity implements Callback<JsonElement> {
    LastNewsAdapter mLNAdapter;
    RecyclerView mNewsRecyclerView;
    public ArrayList<News> mNewsList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private NewsViewModel newsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("categoryId",0);

        swipeRefreshLayout = findViewById(R.id.swipe_news);
        progressBar = findViewById(R.id.progressBar);
        mNewsRecyclerView = findViewById(R.id.lista_noticias);

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_lighter);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.blue_dark);

        swipeRefreshLayout.setOnRefreshListener(() -> newsViewModel.fetchNews());

        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(this);
        layoutManager.setScrollEnabled(true);
        mNewsRecyclerView.setLayoutManager(layoutManager);
        mLNAdapter = new LastNewsAdapter(mNewsList);
        mNewsRecyclerView.setAdapter(mLNAdapter);

        setupToolbar();

        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        newsViewModel.setCategoryId(categoryId);
        newsViewModel.setIsLoading(true);

        newsViewModel.getNewsList().observe(this, news -> {
            mLNAdapter.setItemList((ArrayList<News>) news);
            mLNAdapter.notifyDataSetChanged();
        });

        newsViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                mNewsRecyclerView.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                mNewsRecyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mLNAdapter.setOnClickListener(v ->{
            String url;
            final News newsSelected = mLNAdapter.getItemList().get(mNewsRecyclerView.getChildAdapterPosition(v));
            if (!newsSelected.getUrl().startsWith("http://") && !newsSelected.getUrl().startsWith("https://"))
                url = "http://" + newsSelected.getUrl();
            else
                url = newsSelected.getUrl();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        newsViewModel.fetchNews();
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Noticias");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.finish();
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
        if (response.isSuccessful()){
            assert response.body() != null;
            JsonObject pack = response.body().getAsJsonObject();
            if(pack.has("categories")){
                int id = pack.get("id").getAsInt();
                for (News news:mLNAdapter.getItemList()) {
                    if(id==news.getId() && pack.get("categories").toString().length()>=10) {
                        if (!pack.get("categories").isJsonArray()){
                            ArrayList<String> newsCategories = new ArrayList<>();
                            newsCategories.add(pack.get("categories").getAsString());
                            news.setCategorias(newsCategories);
                            break;
                        }else{
                            ArrayList<String> newsCategories = new ArrayList<>();
                            JsonArray categories = pack.get("categories").getAsJsonArray();
                            for (int i = 0;i<categories.size();i++){
                                newsCategories.add(categories.get(i).getAsString());
                            }
                            news.setCategorias(newsCategories);
                            break;
                        }
                    }
                }
            }else {
                /*int id = pack.get("id").getAsInt();
                for (News news:mLNAdapter.getItemList()) {
                    if(id==news.getId()){
                        news.setImg(pack.get("image").getAsString());
                        break;
                    }

                }*/
            }
            mLNAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }else {
            Log.e("Error", "Respuesta vacia");
        }
    }

    @Override
    public void onFailure(Call<JsonElement> call, Throwable t) {
        Log.e("ioError",call.toString());
    }
}
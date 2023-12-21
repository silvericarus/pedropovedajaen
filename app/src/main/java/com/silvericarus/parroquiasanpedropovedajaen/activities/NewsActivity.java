package com.silvericarus.parroquiasanpedropovedajaen.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.LastNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.CustomGridLayoutManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.Arrays;

import static java.security.AccessController.getContext;

public class NewsActivity extends AppCompatActivity implements Callback<JsonElement> {
    LastNewsAdapter mLNAdapter;
    RecyclerView mNewsRecyclerView;
    public ArrayList<News> mNewsList = new ArrayList<>();
    private Call<JsonElement> callCategories;
    private Call<JsonElement> callImage;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("categoryId",0);
        swipeRefreshLayout = findViewById(R.id.swipe_news);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_lighter);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.blue_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Call<JsonElement> call = ApiAdapter.getApiService().getNewsFromCategory(categoryId);
            call.enqueue(this);
        });
        mNewsRecyclerView = findViewById(R.id.lista_noticias);
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(this);
        layoutManager.setScrollEnabled(true);
        mNewsRecyclerView.setLayoutManager(layoutManager);
        mLNAdapter = new LastNewsAdapter(mNewsList);
        mNewsRecyclerView.setAdapter(mLNAdapter);
        setupToolbar();
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
        RandomImages randomImages = new RandomImages();
        News prueba = new News(0,"Prueba","Si estás viendo esta noticia es que ha ocurrido algún error en la descarga de noticias.", randomImages.getImage(), new ArrayList<>(Arrays.asList( "prueba", "error")),"30/12/1996","www.pedropoveda.es",this);
        mNewsList.add(prueba);
        mLNAdapter.notifyDataSetChanged();
        Call<JsonElement> call = ApiAdapter.getApiService().getNewsFromCategory(categoryId);
        call.enqueue(this);
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
            if (pack.has("news")){
                JsonArray news = pack.getAsJsonArray("news");
                mLNAdapter.getItemList().clear();
                mLNAdapter.notifyDataSetChanged();
                for (int i = 0; i < news.size(); i++) {
                        News news1 = new News();
                        JsonObject row = news.get(i).getAsJsonObject();
                        news1.setId(row.get("ID").getAsInt());
                        news1.setTitle(row.get("post_title").getAsString());
                        Document.OutputSettings outputSettings = new Document.OutputSettings();
                        outputSettings.prettyPrint(false);
                        outputSettings.escapeMode(Entities.EscapeMode.extended);
                        String html = Jsoup.clean(row.get("post_content").getAsString(),"", Whitelist.none(),outputSettings);
                        news1.setContent(StringEscapeUtils.unescapeHtml4(html));
                        String dateAsString = row.get("post_date").getAsString();
                        dateAsString = dateAsString.replace("-","/");
                        dateAsString = dateAsString.replace(dateAsString.substring(dateAsString.indexOf(" ")),"");
                        news1.setFecha(dateAsString);
                        news1.setUrl(row.get("guid").getAsString());
                        callCategories = ApiAdapter.getApiService().getCategoriesFromNew(news1.getId());
                        callImage = ApiAdapter.getApiService().getImageFromNews(news1.getId());
                        callCategories.enqueue(this);
                        callImage.enqueue(this);
                        mLNAdapter.addItemToItemList(news1);
                        mLNAdapter.notifyDataSetChanged();
                }
            }else if(pack.has("categories")){
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
                int id = pack.get("id").getAsInt();
                for (News news:mLNAdapter.getItemList()) {
                    if(id==news.getId()){
                        news.setImg(pack.get("image").getAsString());
                        break;
                    }

                }
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
package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.ColumbariumNewsAdapter;
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

public class TabColumbarium extends Fragment implements Callback<JsonElement> {

    ColumbariumNewsAdapter mCNAdapter;
    RecyclerView mNewsList;
    public ArrayList<News> newsArrayList = new ArrayList<>();
    Context context;
    Call<JsonElement> callImage;
    Call<JsonElement> callCategories;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Call<JsonElement> call = ApiAdapter.getApiService().getColumbariumNews();
        call.enqueue(this);
    }

    public TabColumbarium() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    public static TabColumbarium newInstance(){
        return new TabColumbarium();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_columbarium, container, false);
        mNewsList = view.findViewById(R.id.lista_noticias_columbario);
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(context);
        layoutManager.setScrollEnabled(true);
        mNewsList.setLayoutManager(layoutManager);
        mNewsList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL));
        mCNAdapter = new ColumbariumNewsAdapter(newsArrayList);
        mNewsList.setAdapter(mCNAdapter);
        mCNAdapter.setOnClickListener(view1 -> {
            String url;
            final News newsSelected = mCNAdapter.getItemList().get(mNewsList.getChildAdapterPosition(view1));
            if (!newsSelected.getUrl().startsWith("http://") && !newsSelected.getUrl().startsWith("https://"))
                url = "http://" + newsSelected.getUrl();
            else
                url = newsSelected.getUrl();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
        RandomImages randomImages = new RandomImages();
        News prueba = new News(0,"Prueba","Si estás viendo esta noticia es que ha habido algún error en la descarga de noticias.", randomImages.getImage(), new ArrayList<>(Arrays.asList( "prueba", "error")), "30/12/1996","www.pedropoveda.es",context);
        newsArrayList.add(prueba);
        mCNAdapter.notifyDataSetChanged();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
        if (response.isSuccessful()){
            assert response.body() != null;
            JsonObject pack = response.body().getAsJsonObject();
            if (pack.has("news")) {
                JsonArray news = pack.getAsJsonArray("news");
                mCNAdapter.getItemList().remove(0);
                mCNAdapter.notifyDataSetChanged();
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
                    dateAsString = dateAsString.replace("-", "/");
                    dateAsString = dateAsString.replace(dateAsString.substring(dateAsString.indexOf(" ")), "");
                    news1.setFecha(dateAsString);
                    news1.setUrl(row.get("guid").getAsString());
                    mCNAdapter.addItemToItemList(news1);
                    mCNAdapter.notifyDataSetChanged();
                    callImage = ApiAdapter.getApiService().getImageFromNews(news1.getId());
                    callImage.enqueue(this);
                    callCategories = ApiAdapter.getApiService().getCategoriesFromNew(news1.getId());
                    callCategories.enqueue(this);
                }
            }else if (pack.has("categories")){
                int id = pack.get("id").getAsInt();
                for (News news:mCNAdapter.getItemList()) {
                    if(id==news.getId() && pack.get("categories").toString().length()>=10) {
                        if (!pack.get("categories").isJsonArray()){
                            ArrayList<String> newsCategories = new ArrayList<>();
                            newsCategories.add(pack.get("categories").getAsString());
                            news.setCategorias(newsCategories);
                        }else {
                            Log.i("categories",pack.get("categories").toString());
                            ArrayList<String> newsCategories = new ArrayList<>();
                            JsonArray categories = pack.get("categories").getAsJsonArray();
                            for (int i = 0;i<categories.size();i++){
                                newsCategories.add(categories.get(i).getAsString());
                            }
                            news.setCategorias(newsCategories);
                        }
                        break;
                    }
                }
            }else{
                int id = pack.get("id").getAsInt();
                for (News news:mCNAdapter.getItemList()) {
                    if(id==news.getId()){
                        news.setImg(pack.get("image").getAsString());
                        break;
                    }

                }
            }
            mCNAdapter.notifyDataSetChanged();
        }else {
            Log.e("Error", "Respuesta vacia");
        }
    }

    @Override
    public void onFailure(Call<JsonElement> call, Throwable t) {
        Log.e("ioError",call.toString());
    }
}
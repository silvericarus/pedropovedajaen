package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.telephony.mbms.MbmsErrors;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.ImportantNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.LastNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.CustomGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TabNews extends Fragment implements Callback<JsonElement> {

    ImportantNewsAdapter mINAdapter;
    LastNewsAdapter mLNAdapter;
    RecyclerView mImportantNewsList;
    RecyclerView mLastNewsList;
    public ArrayList<News> importantNewsArrayList = new ArrayList<>();
    public ArrayList<News> lastNewsArrayList = new ArrayList<>();
    AlertDialog.Builder builder;
    Context context;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Call<JsonElement> call = ApiAdapter.getApiService().getLastNews();
        call.enqueue(this);
    }

    public TabNews() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

    }

    public static TabNews newInstance(){
        TabNews fragment = new TabNews();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_news, container, false);
        mImportantNewsList = (RecyclerView) view.findViewById(R.id.lista_noticia_importante);
        mLastNewsList = (RecyclerView) view.findViewById(R.id.lista_ultimas_noticias);
        mImportantNewsList.setLayoutManager(new LinearLayoutManager(context));
        mImportantNewsList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL));
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(context);
        layoutManager.setScrollEnabled(false);
        mLastNewsList.setLayoutManager(layoutManager);
        mLastNewsList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL));
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
        RandomImages randomImages = new RandomImages();
        News prueba = new News(0,"Prueba","Si estás viendo esta noticia es que ha habido algún error en la descarga de noticias.", randomImages.getImage(), new ArrayList<>(Arrays.asList( "prueba", "error")),"30/12/1996","www.pedropoveda.es",context);
        importantNewsArrayList.add(prueba);
        lastNewsArrayList.add(prueba);
        mINAdapter.notifyDataSetChanged();
        mLNAdapter.notifyDataSetChanged();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
        if (response.isSuccessful()){
            assert response.body() != null;
            JsonObject newsPack = response.body().getAsJsonObject();
            JsonArray news = newsPack.getAsJsonArray("news");
            mLNAdapter.getItemList().remove(0);
            mLNAdapter.notifyDataSetChanged();
            for (int i = 0; i < news.size(); i++) {
                News news1 = new News();
                JsonObject row = news.get(i).getAsJsonObject();
                news1.setId(row.get("ID").getAsInt());
                news1.setTitle(row.get("post_title").getAsString());
                news1.setImg(null);
                news1.setFecha(row.get("post_date").getAsString());
                news1.setUrl(row.get("guid").getAsString());
                news1.setCategorias(null);
                mLNAdapter.addItemToItemList(news1);
                mLNAdapter.notifyDataSetChanged();
            }
        }else{
                Log.e("Error","Respuesta vacia");
            }
        }

    @Override
    public void onFailure(Call<JsonElement> call, Throwable t) {
        Log.e("ioError",call.toString());
    }
}


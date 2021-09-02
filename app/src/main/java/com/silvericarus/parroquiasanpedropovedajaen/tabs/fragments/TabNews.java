package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.ImportantNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.LastNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TabNews extends Fragment {

    ImportantNewsAdapter mINAdapter;
    LastNewsAdapter mLNAdapter;
    RequestQueue queue;
    RecyclerView mImportantNewsList;
    RecyclerView mLastNewsList;
    public ArrayList<News> importantNewsArrayList = new ArrayList<>();
    public ArrayList<News> lastNewsArrayList = new ArrayList<>();
    AlertDialog.Builder builder;
    Context context;



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
        queue = Volley.newRequestQueue(context);
        mImportantNewsList = (RecyclerView) view.findViewById(R.id.lista_noticia_importante);
        mLastNewsList = (RecyclerView) view.findViewById(R.id.lista_ultimas_noticias);
        mImportantNewsList.setLayoutManager(new LinearLayoutManager(context));
        mImportantNewsList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL));
        mLastNewsList.setLayoutManager(new LinearLayoutManager(context));
        mLastNewsList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL));
        builder = new AlertDialog.Builder(context);
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
            final News newsSelected = mINAdapter.getItemList().get(mImportantNewsList.getChildAdapterPosition(view1));
            if (!newsSelected.getUrl().startsWith("http://") && !newsSelected.getUrl().startsWith("https://"))
                url = "http://" + newsSelected.getUrl();
            else
                url = newsSelected.getUrl();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

        });
        News prueba = new News(0,"Prueba","Si estás viendo esta noticia es que ha habido algún error en la descarga de noticias.","https://serv3.raiolanetworks.es/blog/wp-content/uploads/error-500-768x499.png", new ArrayList<>(Arrays.asList( "prueba", "error")),new Date(1996,12,30),null,"www.pedropoveda.es");
        importantNewsArrayList.add(prueba);
        lastNewsArrayList.add(prueba);
        mINAdapter.notifyDataSetChanged();
        mLNAdapter.notifyDataSetChanged();
        DownloadNews downloadNews = new DownloadNews();
        downloadNews.execute(importantNewsArrayList,lastNewsArrayList);
        // Inflate the layout for this fragment
        return view;
    }
    public class DownloadNews extends AsyncTask<ArrayList<News>, Void, Void> implements Response.Listener<JSONObject>,Response.ErrorListener{

        @Override
        protected Void doInBackground(ArrayList<News>... arrayLists) {
            final String JSON_URL = "";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,JSON_URL,null,this,this){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type","application/json");
                    return headers;
                }
            };

            queue.add(objectRequest);
            queue.start();
            return null;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("Volley","Respuesta Errónea "+error.toString());
        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONArray news = response.getJSONArray("news");
                for (int i = 0; i < news.length(); i++) {
                    News news1 = new News();
                    JSONObject row = news.getJSONObject(i);
                    news1.setId(row.getInt("id"));
                    news1.setTitle(row.getString("author"));
                    news1.setContent(row.getString("body"));
                    news1.setImg(row.getString("img"));
                    mINAdapter.addItemToItemList(news1);
                    mINAdapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


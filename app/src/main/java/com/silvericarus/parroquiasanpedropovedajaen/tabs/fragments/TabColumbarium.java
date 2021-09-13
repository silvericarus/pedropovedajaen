package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import com.silvericarus.parroquiasanpedropovedajaen.adapters.ColumbariumNewsAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TabColumbarium extends Fragment {

    ColumbariumNewsAdapter mCNAdapter;
    RequestQueue queue;
    RecyclerView mNewsList;
    public ArrayList<News> newsArrayList = new ArrayList<>();
    AlertDialog.Builder builder;
    Context context;

    public TabColumbarium() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    public static TabColumbarium newInstance(){
        TabColumbarium fragment = new TabColumbarium();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_columbarium, container, false);
        queue = Volley.newRequestQueue(context);
        mNewsList = (RecyclerView) view.findViewById(R.id.lista_noticias_columbario);
        mNewsList.setLayoutManager(new LinearLayoutManager(context));
        mNewsList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL));
        builder = new AlertDialog.Builder(context);
        mCNAdapter = new ColumbariumNewsAdapter(newsArrayList);
        mNewsList.setAdapter(mCNAdapter);
        mCNAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final News newsSelected = mCNAdapter.getItemList().get(mNewsList.getChildAdapterPosition(view));

                builder.setTitle(newsSelected.getTitle())
                        .setMessage(HtmlCompat.fromHtml(newsSelected.getContent(), HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH))
                        .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        RandomImages randomImages = new RandomImages();
        News prueba = new News(0,"Prueba","Si estás viendo esta noticia es que ha habido algún error en la descarga de noticias.", randomImages.getImage(), new ArrayList<>(Arrays.asList( "prueba", "error")),"30/12/1996","www.pedropoveda.es",context);
        newsArrayList.add(prueba);
        mCNAdapter.notifyDataSetChanged();
        DownloadNews downloadNews = new DownloadNews();
        //downloadNews.execute(newsArrayList);
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
                    mCNAdapter.addItemToItemList(news1);
                    mCNAdapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
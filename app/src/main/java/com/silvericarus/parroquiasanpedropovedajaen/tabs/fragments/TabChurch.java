package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.CalendarAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.CustomGridLayoutManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;

public class TabChurch extends Fragment implements Callback<JsonElement> {
    Context context;
    ImageButton btnDnJulio, btnFacebook, btnYoutube;
    RecyclerView mHorariosView;
    CalendarAdapter mCAdapter;
    public ArrayList<News> horariosList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Call<JsonElement> call = ApiAdapter.getApiService().getHorario();
        call.enqueue(this);
    }

    public TabChurch() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    public static TabChurch newInstance(){
        return new TabChurch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFull = inflater.inflate(R.layout.fragment_tab_church, container, false);
        swipeRefreshLayout = viewFull.findViewById(R.id.swipe_refresh_church);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_lighter);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.blue_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Call<JsonElement> call = ApiAdapter.getApiService().getHorario();
            call.enqueue(this);
        });
        btnDnJulio = viewFull.findViewById(R.id.btnDnJulio);
        btnFacebook = viewFull.findViewById(R.id.btnFacebook);
        btnYoutube = viewFull.findViewById(R.id.btnYoutube);
        mHorariosView = viewFull.findViewById(R.id.mHorariosView);
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(context);
        layoutManager.setScrollEnabled(false);
        mHorariosView.setLayoutManager(layoutManager);
        mCAdapter = new CalendarAdapter(horariosList);
        mHorariosView.setAdapter(mCAdapter);
        mCAdapter.setOnClickListener(view -> {
            String url;
            final News newsSelected = mCAdapter.getItemList().get(mHorariosView.getChildAdapterPosition(view));
            if (!newsSelected.getUrl().startsWith("http://") && !newsSelected.getUrl().startsWith("https://"))
                url = "http://" + newsSelected.getUrl();
            else
                url = newsSelected.getUrl();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        btnDnJulio.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=34661941531"));
            startActivity(browserIntent);
        });

        btnFacebook.setOnClickListener(view -> {
            Intent intent;
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
            Intent intent;
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

    @Override
    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
        if (response.isSuccessful()){
            assert response.body() != null;
            JsonObject pack = response.body().getAsJsonObject();
            if (pack.has("calendar")){
                mCAdapter.getItemList().clear();
                mCAdapter.notifyDataSetChanged();
                JsonArray news = pack.getAsJsonArray("calendar");
                for (int i = 0; i < news.size(); i++) {
                    News news1 = new News();
                    JsonObject row = news.get(i).getAsJsonObject();
                    news1.setId(row.get("ID").getAsInt());
                    news1.setTitle(row.get("post_title").getAsString());
                    Document.OutputSettings outputSettings = new Document.OutputSettings();
                    outputSettings.prettyPrint(false);
                    outputSettings.escapeMode(Entities.EscapeMode.extended);
                    news1.setContent(Jsoup.clean(row.get("post_content").getAsString(),"", Whitelist.none(),outputSettings));
                    String dateAsString = row.get("post_date").getAsString();
                    dateAsString = dateAsString.replace("-","/");
                    dateAsString = dateAsString.replace(dateAsString.substring(dateAsString.indexOf(" ")),"");
                    news1.setFecha(dateAsString);
                    news1.setUrl(row.get("guid").getAsString());
                    RandomImages randomImages = new RandomImages();
                    news1.setImg(randomImages.getImage());
                    Call<JsonElement> callImage = ApiAdapter.getApiService().getImageFromNews(news1.getId());
                    callImage.enqueue(this);
                    mCAdapter.addItemToItemList(news1);
                    mCAdapter.notifyDataSetChanged();
                }
            }else {
                int id = pack.get("id").getAsInt();
                for (News news:mCAdapter.getItemList()) {
                    if(id==news.getId()){
                        news.setImg(pack.get("image").getAsString());
                        break;
                    }

                }
            }
            mCAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onFailure(Call<JsonElement> call, Throwable t) {
        Log.e("ioError",call.toString());
    }
}
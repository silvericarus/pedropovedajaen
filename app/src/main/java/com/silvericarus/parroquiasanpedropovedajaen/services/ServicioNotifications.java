package com.silvericarus.parroquiasanpedropovedajaen.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;

import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicioNotifications extends Service {
    ServicioNotifications.NotificarNoticias notificarNoticias = new ServicioNotifications.NotificarNoticias();
    SharedPreferences sp;
    NotificationManager notificationManager;
    NotificationCompat.Builder notificationNews26;
    NotificationCompat.Builder notificationNews25;
    private final int TIEMPO = 5000;
    Handler handler = new Handler();
    Call<JsonElement> getLastOneNews = ApiAdapter.getApiService().getLastOneNews();
    SharedPreferences.Editor editor;
    private Call<JsonElement> callCategories;
    private Call<JsonElement> callImage;
    private ArrayList<News> newsList = new ArrayList<>();
    int blue_normal;
    int notID1 = 1;
    NotificationChannel defaultChanel;
    public ServicioNotifications() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        notificarNoticias.hacerEnBackGround();
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        sp = getApplication().getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        editor = sp.edit();
        blue_normal = getResources().getColor(R.color.blue_normal);

        if(Build.VERSION.SDK_INT >= 26) {
            notificationNews26  = new NotificationCompat.Builder(getApplication(), "pedropoveda");
            defaultChanel = new NotificationChannel("pedropoveda", "DefaultChanel", NotificationManager.IMPORTANCE_DEFAULT);
            defaultChanel.enableLights(true);
            defaultChanel.setLightColor(Color.WHITE);
            defaultChanel.setShowBadge(true);
            defaultChanel.enableVibration(true);
            defaultChanel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            defaultChanel.setSound(defaultSoundUri,null);
            notificationManager.createNotificationChannel(defaultChanel);
        }else{
            notificationNews25 = new NotificationCompat.Builder(getApplication());
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class NotificarNoticias implements Callback<JsonElement> {
        ServicioNotifications.NotificarNoticias nn = this;


        void hacerEnBackGround(){
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    getLastOneNews.enqueue(nn);
                }
            },TIEMPO);
        }
        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            if(response.isSuccessful()){
                assert response.body() != null;
                JsonObject pack = response.body().getAsJsonObject();
                if (pack.has("news")){
                    JsonArray news = pack.getAsJsonArray("news");
                    for (int i = 0; i < news.size(); i++) {
                        News news1 = new News();
                        JsonObject row = news.get(i).getAsJsonObject();
                        news1.setId(row.get("ID").getAsInt());
                        news1.setTitle(row.get("post_title").getAsString());
                        Document.OutputSettings outputSettings = new Document.OutputSettings();
                        outputSettings.prettyPrint(false);
                        outputSettings.escapeMode(Entities.EscapeMode.extended);
                        String html = Jsoup.clean(row.get("post_content").getAsString(), "", Whitelist.none(), outputSettings);
                        news1.setContent(StringEscapeUtils.unescapeHtml4(html));
                        String dateAsString = row.get("post_date").getAsString();
                        dateAsString = dateAsString.replace("-", "/");
                        dateAsString = dateAsString.replace(dateAsString.substring(dateAsString.indexOf(" ")), "");
                        news1.setFecha(dateAsString);
                        news1.setUrl(row.get("guid").getAsString());
                        newsList.add(news1);
                        callCategories = ApiAdapter.getApiService().getCategoriesFromNew(news1.getId());
                        callImage = ApiAdapter.getApiService().getImageFromNews(news1.getId());
                        callCategories.enqueue(this);
                        callImage.enqueue(this);
                        if(!sp.getString("lastNewsNotified","1").equals(news1.getUrl())){
                            Log.i("Notify","notified");
                            notifyNews(news1);
                        }
                    }
                }else if(pack.has("categories")){
                    int id = pack.get("id").getAsInt();
                    for (News news:newsList) {
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
                    for (News news:newsList) {
                        if(id==news.getId()){
                            news.setImg(pack.get("image").getAsString());
                            break;
                        }

                    }
                }
            }
        }

        @Override
        public void onFailure(Call<JsonElement> call, Throwable t) {

        }

        public void notifyNews (News news) {
            Log.i("Notify",news.toString());
            if(news.getContent() != null) {
                if(Build.VERSION.SDK_INT >= 26) {
                    notificationNews26.setContentText(news.getContent());
                    notificationNews26.setColor(blue_normal);
                    notificationNews26.setContentTitle(news.getTitle());
                    notificationNews26.setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(news.getContent()));
                    notificationNews26.setSmallIcon(R.mipmap.ic_icon_round);
                    notificationNews26.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationNews26.setChannelId("pedropoveda");
                    notificationManager.notify(notID1, notificationNews26.build());
                }else{
                    notificationNews25.setContentText(news.getContent());
                    notificationNews25.setColor(blue_normal);
                    notificationNews25.setContentTitle(news.getTitle());
                    notificationNews25.setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(news.getContent()));
                    notificationNews25.setSmallIcon(R.mipmap.ic_icon_round);
                    notificationNews25.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationManager.notify(notID1, notificationNews25.build());
                }
                editor.putString("lastNewsNotified", news.getUrl());
                editor.commit();
                notID1++;
            }
        }
    }
}
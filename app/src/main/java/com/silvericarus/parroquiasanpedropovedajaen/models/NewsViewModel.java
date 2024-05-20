package com.silvericarus.parroquiasanpedropovedajaen.models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsViewModel extends ViewModel {
    private MutableLiveData<List<News>> newsList;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private int categoryId;

    public NewsViewModel() {
        newsList = new MutableLiveData<>();
        isLoading.setValue(false);
    }

    public LiveData<List<News>> getNewsList() {
        return newsList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void fetchNews() {
        isLoading.setValue(true);
        Call<JsonElement> call = ApiAdapter.getApiService().getNewsFromCategory(categoryId);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsListData = parseNewsResponse(response.body());
                    newsList.setValue(newsListData);
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("NewsViewModel", "Error fetching news", t);
                isLoading.setValue(false);
            }
        });
    }

    private List<News> parseNewsResponse(JsonElement responseBody) {
        List<News> newsListData = new ArrayList<>();
        JsonObject pack = responseBody.getAsJsonObject();
        if (pack.has("news")) {
            JsonArray news = pack.getAsJsonArray("news");
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy, HH:mm", new Locale("es", "ES"));
            for (int i = 0; i < news.size(); i++) {
                News newsItem = new News();
                JsonObject row = news.get(i).getAsJsonObject();
                newsItem.setId(row.get("ID").getAsInt());
                newsItem.setTitle(row.get("post_title").getAsString());
                Document.OutputSettings outputSettings = new Document.OutputSettings();
                outputSettings.prettyPrint(false);
                outputSettings.escapeMode(Entities.EscapeMode.extended);
                String html = Jsoup.clean(row.get("post_content").getAsString(), "", Whitelist.none(), outputSettings);
                newsItem.setContent(StringEscapeUtils.unescapeHtml4(html));
                String dateAsString = row.get("post_date").getAsString();
                try {
                    Date tmp = inputFormat.parse(dateAsString);
                    String formattedDate = outputFormat.format(tmp);
                    formattedDate = capitalize(formattedDate);
                    newsItem.setFecha(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                newsItem.setUrl(row.get("guid").getAsString());
                newsListData.add(newsItem);
            }
        }
        return newsListData;
    }

    private String capitalize(String input) {
        String[] words = input.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            if (word.length() > 1) {
                capitalizedString.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            } else {
                capitalizedString.append(word.toUpperCase());
            }
            capitalizedString.append(" ");
        }
        return capitalizedString.toString().trim();
    }
}

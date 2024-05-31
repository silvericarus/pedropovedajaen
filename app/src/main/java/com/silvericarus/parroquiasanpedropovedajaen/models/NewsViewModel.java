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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsViewModel extends ViewModel {
    private MutableLiveData<List<News>> newsList;
    private MutableLiveData<List<News>> importantNewsList;
    private MutableLiveData<List<News>> lastNewsList;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private int categoryId;

    public NewsViewModel() {
        newsList = new MutableLiveData<>();
        importantNewsList = new MutableLiveData<>();
        lastNewsList = new MutableLiveData<>();
        isLoading.setValue(false);
    }

    public LiveData<List<News>> getNewsList() {
        return newsList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<News>> getImportantNewsList() {
        return importantNewsList;
    }

    public LiveData<List<News>> getLastNewsList() {
        return lastNewsList;
    }

    public void setIsLoading(boolean b) {
        isLoading.setValue(b);
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
                    List<News> newsListData = parseNewsResponse(response.body(), false, true);
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
    private void updateNewsList(List<News> newsItems, boolean isImportant, boolean isCategorized) {
        if (isImportant) {
            importantNewsList.setValue(newsItems);
        } else if (isCategorized) {
            newsList.setValue(newsItems);
        } else {
            lastNewsList.setValue(newsItems);
        }
    }
    public void fetchMainNews() {
        isLoading.setValue(true);

        Call<JsonElement> callLastNews = ApiAdapter.getApiService().getLastNews();
        Call<JsonElement> callImportantNews = ApiAdapter.getApiService().getImportantNews();

        callLastNews.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsListData = parseNewsResponse(response.body(), false, false);
                    lastNewsList.setValue(newsListData);
                } else {
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                isLoading.setValue(false);
            }
        });

        callImportantNews.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsListData = parseNewsResponse(response.body(), true, false);
                    importantNewsList.setValue(newsListData);
                } else {
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }

    private void fetchAdditionalData(News news, boolean isImportant, boolean isCategorized) {
        Call<JsonElement> callCategories = ApiAdapter.getApiService().getCategoriesFromNew(news.getId());
        Call<JsonElement> callImage = ApiAdapter.getApiService().getImageFromNews(news.getId());

        callCategories.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject pack = response.body().getAsJsonObject();
                    if (pack.has("categories")) {
                        ArrayList<String> categories = new ArrayList<>();
                        if (pack.get("categories").isJsonArray()) {
                            JsonArray categoriesArray = pack.get("categories").getAsJsonArray();
                            for (int i = 0; i < categoriesArray.size(); i++) {
                                categories.add(categoriesArray.get(i).getAsString());
                            }
                        } else {
                            categories.add(pack.get("categories").getAsString());
                        }
                        news.setCategorias(categories);
                    }
                }
                updateNewsListWithAdditionalData(news, isImportant, isCategorized);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("NewsViewModel", "Error fetching categories for news with ID " + news.getId());
            }
        });

        callImage.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject pack = response.body().getAsJsonObject();
                    int id = pack.get("id").getAsInt();
                    if (id == news.getId()) {
                        news.setImg(pack.get("image").getAsString());
                    }
                }
                updateNewsListWithAdditionalData(news, isImportant, isCategorized);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("NewsViewModel", "Error fetching image for news with ID " + news.getId());
            }
        });
        isLoading.setValue(false);
    }

    private void updateNewsListWithAdditionalData(News news, boolean isImportant, boolean isCategorized) {
        if (isImportant) {
            Set<News> currentSet = new HashSet<>(importantNewsList.getValue() != null ? importantNewsList.getValue() : new ArrayList<>());
            currentSet.add(news);
            importantNewsList.setValue(new ArrayList<>(currentSet));
        } else if (isCategorized) {
            Set<News> currentSet = new HashSet<>(newsList.getValue() != null ? newsList.getValue() : new ArrayList<>());
            currentSet.add(news);
            newsList.setValue(new ArrayList<>(currentSet));
        } else {
            Set<News> currentSet = new HashSet<>(lastNewsList.getValue() != null ? lastNewsList.getValue() : new ArrayList<>());
            currentSet.add(news);
            lastNewsList.setValue(new ArrayList<>(currentSet));
        }
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

    private List<News> parseNewsResponse(JsonElement responseBody, boolean isImportant, boolean isCategorized) {
        Set<News> localNewsSet = new HashSet<>();
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
                fetchAdditionalData(newsItem, isImportant, isCategorized);
                localNewsSet.add(newsItem);
            }
            return new ArrayList<>(localNewsSet);
        } else {
            return new ArrayList<>();
        }
    }
}

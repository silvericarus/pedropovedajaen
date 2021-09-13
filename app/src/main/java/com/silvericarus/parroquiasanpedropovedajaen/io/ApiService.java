package com.silvericarus.parroquiasanpedropovedajaen.io;


import com.google.gson.JsonElement;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("?action=get_news")
    Call<JsonElement> getLastNews();


    @GET("?action=get_categories_by_new")
    Call<JsonElement> getCategoriesFromNew(@Query("id") Integer id);
}
package com.silvericarus.parroquiasanpedropovedajaen.io;


import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("?action=get_news")
    Call<JsonElement> getLastNews();


    @GET("?action=get_categories_by_new")
    Call<JsonElement> getCategoriesFromNew(@Query("id") Integer id);

    @GET("?action=get_image_by_new")
    Call<JsonElement> getImageFromNews(@Query("id") Integer id);

    @GET("?action=get_columbario")
    Call<JsonElement> getColumbariumNews();

    @GET("?action=get_destacado")
    Call<JsonElement> getImportantNews();
}
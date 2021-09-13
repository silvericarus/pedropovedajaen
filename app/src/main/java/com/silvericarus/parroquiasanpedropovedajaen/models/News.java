package com.silvericarus.parroquiasanpedropovedajaen.models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class News {
    int id;
    String title;
    String content;
    String img;
    public ArrayList<String> categorias;
    String fecha;
    String url;
    Context context;

    public News(){

    }

    public News(int id, String title, String content, String img, ArrayList<String> categorias, String fecha, String url, Context context) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.img = img;
        this.categorias = categorias;
        this.fecha = fecha;
        this.url = url;
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImg() {
        return img;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public ArrayList<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(ArrayList<String> categorias) {
        this.categorias = categorias;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

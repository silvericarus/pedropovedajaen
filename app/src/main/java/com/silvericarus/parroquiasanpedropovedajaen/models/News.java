package com.silvericarus.parroquiasanpedropovedajaen.models;

import java.util.ArrayList;
import java.util.Date;

public class News {
    int id;
    String title;
    String content;
    String img;
    ArrayList<String> categorias;
    Date fecha;
    String tipo_mime;
    String url;

    public News(){

    }

    public News(int id, String title, String content, String img,ArrayList<String> categorias,Date fecha, String tipo_mime,String url) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.img = img;
        this.categorias = categorias;
        this.fecha = fecha;
        this.tipo_mime = tipo_mime;
        this.url = url;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo_mime() {
        return tipo_mime;
    }

    public void setTipo_mime(String tipo_mime) {
        this.tipo_mime = tipo_mime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

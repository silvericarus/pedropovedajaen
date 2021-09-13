package com.silvericarus.parroquiasanpedropovedajaen.models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class News {
    int id;
    String title;
    String content;
    String img;
    ArrayList<String> categorias;
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
        if (categorias.isEmpty()){
            //GetCategoriasfromWeb getCategoriasfromWeb = new GetCategoriasfromWeb();
            //this.categorias = getCategoriasfromWeb.execute(id);
        }
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

    /*public class GetCategoriasfromWeb extends AsyncTask<Integer,Void,ArrayList<String>> implements Response.Listener<JSONObject>,Response.ErrorListener {

        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            final String JSON_URL = "https://pedropoveda.es/app_views/?action=test";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,JSON_URL,null,this,this){
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
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }

        @Override
        public void onResponse(JSONObject response) {
            Log.i("Volley",response.toString());
        }
    }*/
}

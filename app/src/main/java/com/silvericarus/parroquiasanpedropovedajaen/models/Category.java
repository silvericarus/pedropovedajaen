package com.silvericarus.parroquiasanpedropovedajaen.models;

public class Category {
    int id;
    String name;
    boolean favorite;

    public Category() {
        this.favorite = false;
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        this.favorite = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}

package com.silvericarus.parroquiasanpedropovedajaen.models;

import java.util.Comparator;

public class CategorySort implements Comparator<Category> {
    @Override
    public int compare(Category a, Category b) {
        //TODO Cuando se cambien los ajustes de ordenación, aquí se cambia.
        if (a.getName().equals("Destacado") || b.getName().equals("Destacado")){
            return -404;
        }else {
            return a.getName().compareTo(b.getName());
        }
    }
}

package com.silvericarus.parroquiasanpedropovedajaen.adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.activities.NewsActivity;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomColors;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportantNewsAdapter extends RecyclerView.Adapter<ImportantNewsAdapter.ImportantNewsViewHolder> {

    private ArrayList<News> itemList;
    private View.OnClickListener mListener;

    public ImportantNewsAdapter(ArrayList<News> itemList) {
        this.itemList = itemList;
    }

    public ArrayList<News> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<News> itemList) {
        this.itemList = itemList;
    }

    public void addItemToItemList(News news){
        itemList.add(news);
    }

    @NonNull
    @Override
    public ImportantNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item,parent,false);



        view.setOnClickListener(mListener);

        return new ImportantNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImportantNewsViewHolder holder, int position) {
        holder.bindNewsItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.mListener = listener;
    }


    public void onClick(View v) {
        if(mListener != null){
            mListener.onClick(v);
        }
    }


    public static class ImportantNewsViewHolder extends RecyclerView.ViewHolder implements Callback<JsonElement> {

        private final TextView title;
        private final TextView content;
        private final ImageView img;
        private final TextView fecha;
        private final ChipGroup categories;
        Chip chip;
        String categorySelected;
        SharedPreferences prefs;

        public ImportantNewsViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content_home);
            img = itemView.findViewById(R.id.img);
            fecha = itemView.findViewById(R.id.fecha);
            categories = itemView.findViewById(R.id.category_group);

        }



        public void bindNewsItem(News item) {
            prefs = PreferenceManager.getDefaultSharedPreferences(content.getContext());
            title.setText(item.getTitle());
            content.setText(item.getContent());
            int tamanioElegido = prefs.getInt(content.getContext().getResources().getString(R.string.pref_numArticulos_titulo),20);
            if (tamanioElegido >= 20) {
                content.setTextSize(tamanioElegido);
                title.setTextSize(tamanioElegido);
            } else {
                content.setTextSize(20);
                title.setTextSize(20);
            }
            if (item.getImg() != null){
                if (item.getImg().equals("none") || !item.getImg().startsWith("https")) {
                    RandomImages randomImages = new RandomImages();
                    item.setImg(randomImages.getImage());
                    Uri imgUri = Uri.parse("file:///android_asset/" + item.getImg());
                    Glide.with(img.getContext()).load(imgUri).into(img);
                } else {
                    Glide.with(img.getContext()).load(item.getImg()).into(img);
                }
            }
            fecha.setText(item.getFecha());
            RandomColors randomColors = new RandomColors();
            if (item.getCategorias() != null){
                Log.i("pack1",item.getCategorias().toString());
                categories.removeAllViews();
                for (String categoria : item.getCategorias()) {
                    Chip chip = new Chip(categories.getContext());
                    chip.setText(categoria);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(randomColors.getColor())));
                    chip.setCloseIconVisible(false);
                    chip.setTextColor(Color.BLACK);
                    chip.setTextAppearance(R.style.AppTheme_CategoryChip);
                    chip.setOnClickListener(view -> {
                        categorySelected = ((Chip)view).getText().toString();
                        Call<JsonElement> call = ApiAdapter.getApiService().getCategories();
                        call.enqueue(this);
                    });
                    categories.addView(chip);
                }
            }

        }

        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    JsonArray jsoncategories = response.body().getAsJsonObject().getAsJsonArray("calendar");
                    for (int i = 0; i < jsoncategories.size();i++){
                        JsonObject row = jsoncategories.get(i).getAsJsonObject();
                        for (int j = 0;j<row.size();j++){
                            if (categorySelected.equals(row.get("name").getAsString())){
                                int id = row.get("term_id").getAsInt();
                                Intent intent = new Intent(categories.getContext(), NewsActivity.class);
                                intent.putExtra("categoryId",id);
                                categories.getContext().startActivity(intent);
                                break;
                            }
                        }
                    }
                }
            }

        }

        @Override
        public void onFailure(Call<JsonElement> call, Throwable t) {

        }
    }
}

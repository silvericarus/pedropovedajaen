package com.silvericarus.parroquiasanpedropovedajaen.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomColors;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ColumbariumNewsAdapter extends RecyclerView.Adapter<ColumbariumNewsAdapter.ColumbariumNewsViewHolder> {

    private ArrayList<News> itemList;
    private View.OnClickListener mListener;

    public ColumbariumNewsAdapter(ArrayList<News> itemList) {
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
    public ColumbariumNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item,parent,false);



        view.setOnClickListener(mListener);

        return new ColumbariumNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColumbariumNewsViewHolder holder, int position) {
        holder.bindNewsItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.mListener = listener;
    }


    public void onClick(View v) {
        if(mListener != null){
            mListener.onClick(v);
        }
    }


    public static class ColumbariumNewsViewHolder extends RecyclerView.ViewHolder{

        private final TextView title;
        private final TextView content;
        private final ImageView img;
        private final TextView fecha;
        private final ChipGroup categories;

        public ColumbariumNewsViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content_home);
            img = itemView.findViewById(R.id.img);
            fecha = itemView.findViewById(R.id.fecha);
            categories = itemView.findViewById(R.id.category_group);

        }



        public void bindNewsItem(News item) {
            title.setText(item.getTitle());
            Log.i("new",item.getContent());
            content.setText(item.getContent());
            if (item.getImg() != null){
                if (item.getImg().equals("none") || !item.getImg().startsWith("https")) {
                    RandomImages randomImages = new RandomImages();
                    item.setImg(randomImages.getImage());
                    Log.i("img1",item.getImg());
                    Uri imgUri = Uri.parse("file:///android_asset/" + item.getImg());
                    Glide.with(img.getContext()).load(imgUri).into(img);
                } else {
                    Glide.with(img.getContext()).load(item.getImg()).into(img);
                }
            }
            fecha.setText(item.getFecha());
            RandomColors randomColors = new RandomColors();
            if (item.getCategorias() != null){
                categories.removeAllViews();
                if (item.getCategorias().size() == 1){
                    Chip chip = new Chip(categories.getContext());
                    chip.setText(item.getCategorias().get(0));
                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(randomColors.getColor())));
                    chip.setCloseIconVisible(false);
                    chip.setTextColor(Color.BLACK);
                    chip.setTextAppearance(R.style.TextAppearance_MaterialComponents_Chip);
                    categories.addView(chip);
                }
            }
        }
    }
}

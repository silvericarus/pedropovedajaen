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

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LastNewsAdapter extends RecyclerView.Adapter<LastNewsAdapter.LastNewsViewHolder> {

    private ArrayList<News> itemList;
    private View.OnClickListener mListener;

    public LastNewsAdapter(ArrayList<News> itemList) {
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
    public LastNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item,parent,false);



        view.setOnClickListener(mListener);

        LastNewsViewHolder viewHolder = new LastNewsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LastNewsViewHolder holder, int position) {
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


    public class LastNewsViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView content;
        private ImageView img;
        private TextView fecha;
        private ChipGroup categories;

        public LastNewsViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            img = (ImageView) itemView.findViewById(R.id.img);
            fecha = (TextView) itemView.findViewById(R.id.fecha);
            categories = (ChipGroup) itemView.findViewById(R.id.category_group);

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
                for (String categoria : item.getCategorias()) {
                    Chip chip = new Chip(categories.getContext());
                    chip.setText(categoria);
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

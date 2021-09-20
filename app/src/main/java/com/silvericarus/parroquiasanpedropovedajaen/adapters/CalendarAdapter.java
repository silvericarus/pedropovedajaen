package com.silvericarus.parroquiasanpedropovedajaen.adapters;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.models.News;
import com.silvericarus.parroquiasanpedropovedajaen.models.RandomImages;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    private ArrayList<News> itemList;
    private View.OnClickListener mListener;

    public CalendarAdapter(ArrayList<News> itemList) {
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
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item_calendar,parent,false);

        view.setOnClickListener(mListener);

        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.bindCalendarItem(itemList.get(position));
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

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView content;
        private final ImageView img;
        SharedPreferences prefs;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content_home);
            img = itemView.findViewById(R.id.img);
        }

        public void bindCalendarItem(News news) {
            prefs = PreferenceManager.getDefaultSharedPreferences(content.getContext());
            title.setText(news.getTitle());
            content.setText(news.getContent());
            int tamanioElegido = prefs.getInt(img.getContext().getResources().getString(R.string.pref_numArticulos_titulo),20);
            if (tamanioElegido >= 20) {
                content.setTextSize(tamanioElegido);
                title.setTextSize(tamanioElegido);
            } else {
                content.setTextSize(20);
                title.setTextSize(20);
            }
            if (news.getImg() != null){
                if (news.getImg().equals("none") || !news.getImg().startsWith("https")) {
                    Uri imgUri = Uri.parse("file:///android_asset/" + news.getImg());
                    Glide.with(img.getContext()).load(imgUri).into(img);
                } else {
                    Glide.with(img.getContext()).load(news.getImg()).into(img);
                }
            }
        }
    }
}

package com.silvericarus.parroquiasanpedropovedajaen.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.models.Category;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> implements View.OnLongClickListener {

    private ArrayList<Category> itemList;
    private View.OnClickListener mListener;
    private View.OnLongClickListener mLListener;

    public CategoriesAdapter(ArrayList<Category> itemList) {
        this.itemList = itemList;
    }

    public ArrayList<Category> getItemList() {
        return itemList;
    }

    public void addItemToItemList(Category category){
        itemList.add(category);
    }

    public void setItemList(ArrayList<Category> itemList) {
        this.itemList = itemList;
    }



    @NonNull
    @Override
    public CategoriesAdapter.CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item,parent,false);


        view.setOnLongClickListener(mLListener);
        view.setOnClickListener(mListener);

        return new CategoriesAdapter.CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoriesViewHolder holder, int position) {
        holder.bindCategoriesItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {return itemList != null ? itemList.size() : 0;}

    public void setOnClickListener(View.OnClickListener listener){
        this.mListener = listener;
    }

    public void setOnLongClickListener(View.OnLongClickListener listener){ this.mLListener = listener; }

    public void onClick(View v) {
        if(mListener != null){
            mListener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mLListener != null){
            mLListener.onLongClick(view);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
        }

        public void bindCategoriesItem(Category category) {
            name.setText(category.getName());
        }
    }
}

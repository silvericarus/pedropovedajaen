package com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.activities.NewsActivity;
import com.silvericarus.parroquiasanpedropovedajaen.adapters.CategoriesAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.io.ApiAdapter;
import com.silvericarus.parroquiasanpedropovedajaen.models.Category;
import com.silvericarus.parroquiasanpedropovedajaen.models.CategorySort;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.CustomGridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabCategories extends Fragment implements Callback<JsonElement> {
    Context context;
    CategoriesAdapter mCAdapter;
    RecyclerView mCategoriesList;
    public ArrayList<Category> categoryArrayList = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Call<JsonElement> call = ApiAdapter.getApiService().getCategories();
        call.enqueue(this);
    }

    public TabCategories() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    public static TabCategories newInstance(){
        return new TabCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_categories, container, false);
        mCategoriesList = view.findViewById(R.id.lista_categorias);
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(context);
        layoutManager.setScrollEnabled(true);
        mCategoriesList.setLayoutManager(layoutManager);
        mCategoriesList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL));
        mCAdapter = new CategoriesAdapter(categoryArrayList);
        mCategoriesList.setAdapter(mCAdapter);
        mCAdapter.setOnClickListener(v -> {
            int id;
            final Category categorySelected = mCAdapter.getItemList().get(mCategoriesList.getChildAdapterPosition(v));
            id = categorySelected.getId();
            Intent intent = new Intent(context, NewsActivity.class);
            intent.putExtra("categoryId",id);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
        if (response.isSuccessful()){
            if (response.body()!=null){
                JsonObject pack = response.body().getAsJsonObject();
                JsonArray categories = pack.getAsJsonArray("calendar");
                for (int i=0; i<categories.size(); i++){
                    Category category = new Category();
                    JsonObject row = categories.get(i).getAsJsonObject();
                    category.setId(row.get("term_id").getAsInt());
                    category.setName(row.get("name").getAsString());
                    mCAdapter.addItemToItemList(category);
                    mCAdapter.notifyDataSetChanged();
                }
            }else{
                Log.e(String.valueOf(R.string.error),"response empty");
            }
        }else{
            Log.e(String.valueOf(R.string.error),"response not successful");
        }
        Collections.sort(categoryArrayList,new CategorySort());
        mCAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Call<JsonElement> call, Throwable t) {
        Log.e(String.valueOf(R.string.error),call.toString());
    }
}

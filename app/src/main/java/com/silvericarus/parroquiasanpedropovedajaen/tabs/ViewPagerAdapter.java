package com.silvericarus.parroquiasanpedropovedajaen.tabs;

import com.silvericarus.parroquiasanpedropovedajaen.R;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments.TabChurch;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments.TabColumbarium;
import com.silvericarus.parroquiasanpedropovedajaen.tabs.fragments.TabNews;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {


    @Override
    public int getItemCount() {
        return Tab.values().length;
    }

    public enum Tab {
        NEWS(0,R.string.tab_news),
        COLUMBARIUM(1, R.string.tab_columbarium),
        CHURCH(2,R.string.tab_church);
        final public int title;
        final public int position;

        Tab(int position, @StringRes int title){
            this.position = position;
            this.title = title;
        }

        private static final Map<Integer,Tab> map;

        static {
            map = new HashMap<>();
            for (Tab t : Tab.values()){
                map.put(t.position,t);
            }
        }

        public static Tab byPosition(int position){
            return map.get(position);
        }


    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    public Fragment createFragment(int position) {
        if (position == Tab.COLUMBARIUM.position){
            return TabColumbarium.newInstance();
        }else if(position == Tab.NEWS.position) {
            return TabNews.newInstance();
        }else if (position == Tab.CHURCH.position) {
            return TabChurch.newInstance();
        }else {
            throw new IllegalArgumentException("unknown position " + position);
        }
    }



}



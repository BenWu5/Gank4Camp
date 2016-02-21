package com.ben.gank.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ted on 2015/8/24.
 */
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    private  List<Fragment> mFragments = null;
    private  List<String> mFragmentTitles = null;
    private FragmentManager mFragmentManager;

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mFragmentTitles = new ArrayList<>();
        mFragmentManager = fm;

    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }
    public void clear(){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for(Fragment f:this.mFragments){
            ft.remove(f);
        }
        ft.commit();
        mFragmentManager.executePendingTransactions();

        mFragments.clear();
        mFragmentTitles.clear();

    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}

package com.ben.gank.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ben.gank.Config;
import com.ben.gank.R;
import com.ben.gank.activity.SortActivity;
import com.ben.gank.adapter.HomeFragmentPagerAdapter;
import com.ben.gank.model.Type;
import com.ben.gank.realm.DataService;
import com.ben.gank.realm.RealmDataService;
import com.ben.gank.utils.TypeUtils;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import butterknife.Bind;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeFragment extends BaseFragment {

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    DataService mDataService;

    //Drag RecyclerView
    private HomeFragmentPagerAdapter mTypeFragmentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mDataService = new RealmDataService(getContext());
        loadType();
    }

    protected void loadType() {
        mDataService.getVisibilityTypeList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<Type>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Observer<List<Type>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackbar(e.getMessage());
                        Log.i("showSnackbar",e.getMessage());
                    }

                    @Override
                    public void onNext(List<Type> types) {
                        if (null != types && !types.isEmpty()) {
                            if (mTypeFragmentAdapter == null) {
                                mTypeFragmentAdapter = new HomeFragmentPagerAdapter(getChildFragmentManager());
                                mViewPager.setAdapter(mTypeFragmentAdapter);
                            } else {
                                //先清空Fragment
                                mTypeFragmentAdapter.clear();
                            }

                            //根据类型添加fragment
                            for (Type type : types) {
                                if (type.getTitle().equals(Config.TYPE_GIRL)) {
                                    mTypeFragmentAdapter.addFragment(GirlFragment.newFragment(type.getTitle()), getContext().getString(TypeUtils.getTypeString(type.getTitle())));
                                } else {
                                    mTypeFragmentAdapter.addFragment(GankListFragment.newFragment(type.getTitle()), getContext().getString(TypeUtils.getTypeString(type.getTitle())));
                                }
                            }
                            mTypeFragmentAdapter.notifyDataSetChanged();
                            mViewPager.setOffscreenPageLimit(mTypeFragmentAdapter.getCount());
                            mTabLayout.setupWithViewPager(mViewPager);
                            mViewPager.setCurrentItem(0);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SORT_CODE && resultCode == Activity.RESULT_OK) {
            loadType();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static int REQUEST_SORT_CODE = 0x123;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            Intent intent = new Intent(getActivity(), SortActivity.class);
            startActivityForResult(intent, REQUEST_SORT_CODE);
            getActivity().overridePendingTransition(R.anim.in_from_right, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

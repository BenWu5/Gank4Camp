package com.ben.gank.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.ben.gank.Config;
import com.ben.gank.R;
import com.ben.gank.activity.ImageActivity;
import com.ben.gank.adapter.GirlAdapter;
import com.ben.gank.model.Data;
import com.ben.gank.model.Image;
import com.ben.gank.net.GankApi;
import com.ben.gank.realm.DataService;
import com.ben.gank.realm.RealmDataService;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GirlFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String KEY_TYPE = "type";
    private GirlAdapter mBenefitItemAdapter;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_ly)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int currentPager = 1;
    private boolean isLoadingNewData = false;
    private boolean isLoadingMore = false;
    private boolean isALlLoad = false;

    private DataService mDataService;


    private Observer<List<Image>> dataObserver = new Observer<List<Image>>() {
        @Override
        public void onNext(final List<Image> imageList) {
            if (null != imageList && !imageList.isEmpty()) {
                if (imageList.size() < GankApi.LOAD_LIMIT) {
                    isALlLoad = true;
                    showSnackbar(R.string.no_more);
                }
                if (isLoadingMore) {
                    currentPager++;
                    mBenefitItemAdapter.addAll(imageList);
                } else if (isLoadingNewData) {
                    isALlLoad = false;
                    currentPager = 1;
                    mBenefitItemAdapter.replaceWith(imageList);
                }
            }
        }

        @Override
        public void onCompleted() {
            mSwipeRefreshLayout.setRefreshing(false);
            isLoadingNewData = false;
            isLoadingMore = false;
        }

        @Override
        public void onError(final Throwable error) {
            mSwipeRefreshLayout.setRefreshing(false);
            isLoadingNewData = false;
            isLoadingMore = false;
            showSnackbar("OnError");
        }
    };


    public static GirlFragment newFragment(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        GirlFragment fragment = new GirlFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        setUpRecyclerView(mRecyclerView);
        mBenefitItemAdapter = getGankAdapter();
        new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setAdapter(mBenefitItemAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        setSwipeRefreshLayout(mSwipeRefreshLayout);
        mDataService =new RealmDataService(getContext());
        requestRefresh();
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredGridLayoutManager);

    }

    private void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        Resources resources = getResources();
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.blue_dark),
                resources.getColor(R.color.red_dark),
                resources.getColor(R.color.yellow_dark),
                resources.getColor(R.color.green_dark)
        );
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    private GirlAdapter getGankAdapter() {
        return new GirlAdapter(getActivity()) {
            @Override
            protected void onItemClick(View v, int position) {
                ImageActivity.startImageActivity(getContext(), mBenefitItemAdapter.get(position).getUrl());
            }
        };
    }

    private void loadData(int pager) {
        GankApi.getIns()
                .getCommonGoods(Config.TYPE_GIRL, GankApi.LOAD_LIMIT, pager)
                .compose(this.<Data>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .cache()
                .flatMap(new Func1<Data, Observable<List<Image>>>() {
                    @Override
                    public Observable<List<Image>> call(Data data) {
                        return mDataService.addImageList(data.getResults());
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataObserver);
    }


    @Override
    public void onRefresh() {
        requestRefresh();
    }

    private void requestMoreData() {
        int nextPager = currentPager + 1;
        requestMoreData(nextPager);

    }

    private void requestMoreData(int pager) {
        if(isLoading()){
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        isLoadingMore = true;
        isLoadingNewData = false;
        loadData(pager);

    }

    private void requestRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        isLoadingMore = false;
        isLoadingNewData = true;
        loadData(1);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int[] positions = new int[mStaggeredGridLayoutManager.getSpanCount()];
            mStaggeredGridLayoutManager.findLastVisibleItemPositions(positions);
            for (int position : positions) {
                if (position == mStaggeredGridLayoutManager.getItemCount() - 1&&!isALlLoad) {
                    requestMoreData();
                    break;
                }
            }
        }
    };

    private boolean isLoading() {
        return isLoadingMore || isLoadingNewData;
    }


}

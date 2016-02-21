package com.ben.gank.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ben.gank.Config;
import com.ben.gank.R;
import com.ben.gank.adapter.GankAdapter;
import com.ben.gank.model.Data;
import com.ben.gank.net.GankApi;
import com.ben.gank.view.DividerDecoration;
import com.ben.gank.view.SectionsDecoration;
import com.ben.gank.view.SwipeToRefreshLayout;
import com.trello.rxlifecycle.FragmentEvent;

import butterknife.Bind;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GankListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String KEY_TYPE = "type";
    private GankAdapter mGankAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_ly)
    SwipeToRefreshLayout mSwipeRefreshLayout;
    //当前页数
    private int currentPager = 1;
    //是否刷新状态
    private boolean isLoadingNewData = false;
    //是否载入更多状态
    private boolean isLoadingMore = false;
    //是否已经载入去全部
    private boolean isALlLoad = false;
    //类型
    private String mType = null;

    private Observer<Data> dataObserver = new Observer<Data>() {
        @Override
        public void onNext(final Data goodsResult) {
            if (null != goodsResult && null != goodsResult.getResults()) {
                //如果取得的Results小于 预先设定的数量（GankApi.LOAD_LIMIT）就表示已经是最后一页
                if (goodsResult.getResults().size() < GankApi.LOAD_LIMIT) {
                    isALlLoad = true;
                    showSnackbar(R.string.no_more);
                }
                if (isLoadingMore) {
                    currentPager++;
                    mGankAdapter.addData(goodsResult.getResults());
                } else if (isLoadingNewData) {
                    isALlLoad = false;
                    currentPager = 1;
                    mGankAdapter.setData(goodsResult.getResults());
                }
                mGankAdapter.notifyDataSetChanged();
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

    public static GankListFragment newFragment(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        GankListFragment fragment = new GankListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getString(KEY_TYPE, Config.TYPE_ANDROID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        setUpRecyclerView(mRecyclerView);
        mGankAdapter = getGankAdapter();
        mRecyclerView.setAdapter(mGankAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        setSwipeRefreshLayout(mSwipeRefreshLayout);
        requestRefresh();
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mLinearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //分割线
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()).setPaddingLeft(getResources().getDimensionPixelSize(R.dimen.item_margin)));
        //日期说明
        recyclerView.addItemDecoration(new SectionsDecoration(true));
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


    private GankAdapter getGankAdapter() {
        return new GankAdapter(getActivity());
    }

    private void loadData(int pager) {
        GankApi.getIns()
                .getCommonGoods(mType, GankApi.LOAD_LIMIT, pager)
                .compose(this.<Data>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .cache()
                .subscribeOn(Schedulers.io())
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
            //但RecyclerView滑动到倒数第三个之请求加载更多
            int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            int totalItemCount = mGankAdapter.getItemCount();
            // dy>0 表示向下滑动
            if (lastVisibleItem >= totalItemCount - 3 && dy > 0 && !isLoading() && !isALlLoad) {
                requestMoreData();
            }
        }
    };

    private boolean isLoading() {
        return isLoadingMore || isLoadingNewData;
    }


}

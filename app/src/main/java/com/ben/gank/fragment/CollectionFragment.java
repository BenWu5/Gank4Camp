package com.ben.gank.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ben.gank.Config;
import com.ben.gank.R;
import com.ben.gank.adapter.BookmarkAdapter;
import com.ben.gank.model.Bookmark;
import com.ben.gank.realm.DataService;
import com.ben.gank.realm.RealmDataService;
import com.ben.gank.view.DividerDecoration;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import butterknife.Bind;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CollectionFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private LinearLayoutManager mLinearLayoutManager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.spinner)
    AppCompatSpinner mSpinner;

    @Bind(R.id.empty_view)
    TextView mEmptyView;

    private BookmarkAdapter mBookmarkAdapter;
    private DataService mDataService;
    private CompositeSubscription mCompositeSubscription;

    private Observer<List<Bookmark>> dataObserver = new Observer<List<Bookmark>>() {

        @Override
        public void onNext(List<Bookmark> bookmarks) {
            mBookmarkAdapter.setData(bookmarks);
            mBookmarkAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            showSnackbar(e.getMessage());
        }

    };
    private RecyclerView.AdapterDataObserver emptyObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mBookmarkAdapter != null) {
                if (mBookmarkAdapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }

        }
    };

    private void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mLinearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()).setPaddingLeft(getResources().getDimensionPixelSize(R.dimen.item_margin)));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collection;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setUpRecyclerView(mRecyclerView);
        mBookmarkAdapter = new BookmarkAdapter(getContext());
        mBookmarkAdapter.registerAdapterDataObserver(emptyObserver);
        mRecyclerView.setAdapter(mBookmarkAdapter);
        mDataService = new RealmDataService(getContext());
        mCompositeSubscription = new CompositeSubscription();
        mSpinner.setOnItemSelectedListener(this);
    }


    private void qurey(String type) {
        mDataService.getBookmarkList(type).subscribeOn(Schedulers.io())
                .compose(this.<List<Bookmark>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread()).subscribe(dataObserver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_collection, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //搜索功能  未完成
        //if (id == R.id.action_search) {
        //   return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                qurey(null);
                break;
            case 1:
                qurey(Config.TYPE_ANDROID);
                break;
            case 2:
                qurey(Config.TYPE_IOS);
                break;
            case 3:
                qurey(Config.TYPE_RECOMMEND);
                break;
            case 4:
                qurey(Config.TYPE_RESOURCES);
                break;
            case 5:
                qurey(Config.TYPE_FRONT_END);
                break;
            case 6:
                qurey(Config.TYPE_VIDEO);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

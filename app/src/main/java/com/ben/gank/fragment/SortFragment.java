

package com.ben.gank.fragment;

import android.app.Activity;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ben.gank.R;
import com.ben.gank.activity.base.AppSwipeBackActivity;
import com.ben.gank.adapter.SortTypeAdapter;
import com.ben.gank.model.Type;
import com.ben.gank.realm.DataService;
import com.ben.gank.realm.RealmDataService;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SortFragment extends BaseFragment {

    //Drag RecyclerView
    private SortTypeAdapter mSortTypeAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    //Data
    private DataService mDataService;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.save_fab)
    FloatingActionButton mFab;


    private Observer<List<Type>> getObserver = new Observer<List<Type>>() {
        @Override
        public void onNext(final List<Type> list) {
            if (null != list && !list.isEmpty()) {
                mSortTypeAdapter.typeList2ItemList(list);
                mSortTypeAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(final Throwable error) {
            showSnackbar("onError");
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sort;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mDataService = new RealmDataService(getContext());
        initView();
        loadData();
    }

    protected void initView() {
        mToolbar.setTitle(getString(R.string.sort));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z1));

        //adapter
        mSortTypeAdapter = new SortTypeAdapter(getActivity());

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mSortTypeAdapter);      // wrap for dragging

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z1)));
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSortTypeAdapter.isVisibilityListEmpty()) {
                    showSnackbar("至少选择一个类型");
                } else {
                    updateTypeList(mSortTypeAdapter.itemList2TypeList());
                }
            }
        });

    }

    private void loadData() {
        query().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver);
    }

    private void updateTypeList(List<Type> list) {
        mDataService.updateTypeList(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Boolean>bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            getActivity().setResult(Activity.RESULT_OK);
                            ((AppSwipeBackActivity) getActivity()).scrollToFinishActivity();
                        }
                    }
                });
    }

    protected Observable<List<Type>> query() {
        return mDataService.getAllTypeList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((AppSwipeBackActivity) getActivity()).scrollToFinishActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        super.onDestroyView();
    }

}
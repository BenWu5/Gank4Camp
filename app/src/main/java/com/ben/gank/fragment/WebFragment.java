/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ben.gank.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ben.gank.R;
import com.ben.gank.activity.base.AppSwipeBackActivity;
import com.ben.gank.model.Bookmark;
import com.ben.gank.realm.DataService;
import com.ben.gank.realm.RealmDataService;
import com.ben.gank.utils.SystemUtils;
import com.trello.rxlifecycle.FragmentEvent;

import butterknife.Bind;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WebFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String KEY_ID = "key_id";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_URL = "key_url";
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_WHO = "key_who";

    private DataService dataService;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Bind(R.id.web_view)
    WebView mWebView;

    private String mObjectId;
    private String mTitle;
    private String mURl;
    private String mType;
    private String mWho;

    MenuItem itemCollect;
    boolean isCollected = false;

    public static WebFragment newInstance(String id, String title, String url, String type, String who) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ID, id);
        bundle.putSerializable(KEY_TITLE, title);
        bundle.putSerializable(KEY_URL, url);
        bundle.putSerializable(KEY_TYPE, type);
        bundle.putSerializable(KEY_WHO, who);
        WebFragment Fragment = new WebFragment();
        Fragment.setArguments(bundle);
        return Fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mObjectId = getArguments().getString(KEY_ID);
        mTitle = getArguments().getString(KEY_TITLE);
        mType = getArguments().getString(KEY_TYPE);
        mURl = getArguments().getString(KEY_URL);
        mWho = getArguments().getString(KEY_WHO);

        initView();
        initWebView();

        getActivity().getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                getActivity().getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        dataService = new RealmDataService(getContext());
        mWebView.loadUrl(mURl);
        mSwipeRefreshLayout.setRefreshing(true);

    }

    protected void initView() {
        mToolbar.setSubtitle(mTitle);
        mToolbar.setTitle("");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Resources resources = getResources();
        mSwipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.blue_dark),
                resources.getColor(R.color.red_dark),
                resources.getColor(R.color.yellow_dark),
                resources.getColor(R.color.green_dark)

        );
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    if (mProgressBar != null && mSwipeRefreshLayout != null) {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBar.setProgress(0);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    // 加载中
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress(newProgress);
                    }
                }
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDisplayZoomControls(true);

    }

    private void getIsCollect() {
         dataService.findBookmarkById(mObjectId)
                 .compose(this.<Bookmark>bindUntilEvent(FragmentEvent.PAUSE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bookmark>() {
                    @Override
                    public void onCompleted() {
                        itemCollect.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackbar(e.getLocalizedMessage());
//                        Toast.makeText(getContext(), "onError", Toast.LENGTH_LONG).show();
                        Log.i("getIsCollect onError", e.getMessage());
                    }

                    @Override
                    public void onNext(Bookmark bookmark) {
                        if (bookmark == null) {
                            itemCollect.setIcon(R.drawable.ic_star_outline_grey600_24dp);
                            isCollected = false;
                        } else {
                            itemCollect.setIcon(R.drawable.ic_star_grey600_24dp);
                            isCollected = true;
                        }
                    }
                });
    }

    private void collect() {
        itemCollect.setEnabled(false);
        if (isCollected) {
            dataService.removeBookmark(mObjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bookmark>() {
                        @Override
                        public void call(Bookmark bookmark) {
                            getIsCollect();
                        }
                    });

        } else {
            dataService.addBookmark(new Bookmark(mObjectId, System.currentTimeMillis(), mTitle, mType, mURl,mWho))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bookmark>() {
                        @Override
                        public void call(Bookmark bookmark) {
                            getIsCollect();
                        }
                    });
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            ((AppSwipeBackActivity) getActivity()).scrollToFinishActivity();
        } else if (id == R.id.action_copy_the_url) {
            SystemUtils.copyText(getContext(), mURl);
            showSnackbar(R.string.copied_to_clipboard);
        } else if (id == R.id.action_open_in_browser) {
            SystemUtils.openUrlByBrowser(getContext(),mURl);
        } else if (id == R.id.action_collect) {
            collect();
        } else if (id == R.id.action_share) {
            SystemUtils.share(getContext(), mURl, mTitle);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_web, menu);
        itemCollect = menu.findItem(R.id.action_collect);
        itemCollect.setEnabled(false);
        getIsCollect();
        super.onCreateOptionsMenu(menu, inflater);
    }



    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }

    @Override
    public void onRefresh() {
        mWebView.reload();
    }
}
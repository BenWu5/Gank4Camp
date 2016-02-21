package com.ben.gank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.ben.gank.R;
import com.ben.gank.activity.base.SingleFragmentActivity;
import com.ben.gank.fragment.WebFragment;
import com.ben.gank.model.Bookmark;
import com.ben.gank.model.Result;


public class WebActivity extends SingleFragmentActivity {

    private WebFragment mWebFragment;

    @Override
    protected Fragment getFragment() {
        mWebFragment = new WebFragment();
        return mWebFragment;
    }

    @Override
    protected Bundle getArguments() {
        Intent intent = getIntent();
        String id = intent.getStringExtra(WebFragment.KEY_ID);
        String title = intent.getStringExtra(WebFragment.KEY_TITLE);
        String type = intent.getStringExtra(WebFragment.KEY_TYPE);
        String url = intent.getStringExtra(WebFragment.KEY_URL);
        String who = intent.getStringExtra(WebFragment.KEY_WHO);

        Bundle bundle = new Bundle();
        bundle.putSerializable(WebFragment.KEY_ID, id);
        bundle.putSerializable(WebFragment.KEY_TITLE, title);
        bundle.putSerializable(WebFragment.KEY_URL, url);
        bundle.putSerializable(WebFragment.KEY_TYPE, type);
        bundle.putSerializable(WebFragment.KEY_WHO, who);
        return bundle;

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(mWebFragment.canGoBack()){
                mWebFragment.goBack();
            }else{
                scrollToFinishActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static void startWebActivity(Context context, Result result) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WebFragment.KEY_ID, result.getObjectId());
        intent.putExtra(WebFragment.KEY_TITLE, result.getDesc());
        intent.putExtra(WebFragment.KEY_TYPE, result.getType());
        intent.putExtra(WebFragment.KEY_URL, result.getUrl());
        intent.putExtra(WebFragment.KEY_WHO, result.getWho());
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.in_from_right, 0);
    }
    public static void startWebActivity(Context context, Bookmark bookmark) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WebFragment.KEY_ID, bookmark.getObjectId());
        intent.putExtra(WebFragment.KEY_TITLE, bookmark.getDesc());
        intent.putExtra(WebFragment.KEY_TYPE, bookmark.getType());
        intent.putExtra(WebFragment.KEY_URL, bookmark.getUrl());
        intent.putExtra(WebFragment.KEY_WHO, bookmark.getWho());
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.in_from_right, 0);
    }

}

package com.ben.gank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.ben.gank.R;
import com.ben.gank.activity.base.SingleFragmentActivity;
import com.ben.gank.fragment.ImageFragment;


public class ImageActivity extends SingleFragmentActivity {

    public static void startImageActivity(Context context, String url) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(ImageFragment.KEY_URL,url);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.in_from_right, 0);
    }


    @Override
    protected Fragment getFragment() {
        return new ImageFragment();
    }

    @Override
    protected Bundle getArguments() {
        String url = getIntent().getStringExtra(ImageFragment.KEY_URL);
        Bundle bundle = new Bundle();
        bundle.putString(ImageFragment.KEY_URL, url);
        return bundle;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

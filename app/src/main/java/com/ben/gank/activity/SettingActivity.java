package com.ben.gank.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.ben.gank.activity.base.SingleFragmentActivity;
import com.ben.gank.fragment.SettingFragment;

public class SettingActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return new SettingFragment();
    }

    @Override
    protected Bundle getArguments() {
        return null;
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

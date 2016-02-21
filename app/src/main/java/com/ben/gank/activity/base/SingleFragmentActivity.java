package com.ben.gank.activity.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ben.gank.R;


public abstract class SingleFragmentActivity extends AppSwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        Fragment fragment = getFragment();
        fragment.setArguments(getArguments());
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();

    }


    protected abstract Fragment getFragment();

    protected abstract Bundle getArguments() ;
}

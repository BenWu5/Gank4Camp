package com.ben.gank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ben.gank.R;
import com.ben.gank.fragment.CollectionFragment;
import com.ben.gank.fragment.HomeFragment;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ;
    public static final String KEY_STATUS_FRAGMENT_INDEX = "key_status_fragment_index";

    private static final int HOME_INDEX = 0;
    private static final int COLLECTION_INDEX = 1;

    private int currentFragmentIndex = HOME_INDEX;

    @Bind(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            currentFragmentIndex = savedInstanceState.getInt(KEY_STATUS_FRAGMENT_INDEX);
        }

        ButterKnife.bind(this);
        mNavigationView.setNavigationItemSelectedListener(this);

        ImageView drawHeaderImageView = (ImageView) mNavigationView.getHeaderView(0);
        Glide.with(this).load(R.drawable.bg_nv_header).into(drawHeaderImageView);

        updateFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_STATUS_FRAGMENT_INDEX, currentFragmentIndex);
    }

    public void updateFragment() {
        switch (currentFragmentIndex) {
            case HOME_INDEX:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                break;
            case COLLECTION_INDEX:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new CollectionFragment()).commit();
                break;
        }
    }

    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        initDrawerLayout(toolbar);
    }


    private void initDrawerLayout(Toolbar toolbar) {
        // 實作 drawer toggle 並放入 toolbar
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.syncState();
        mDrawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                currentFragmentIndex = HOME_INDEX;
                updateFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                item.setChecked(true);
                break;
            case R.id.nav_collection:
                currentFragmentIndex = COLLECTION_INDEX;
                updateFragment();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                item.setChecked(true);
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                this.overridePendingTransition(R.anim.in_from_right, 0);
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }else{
                finish();
                overridePendingTransition(0, R.anim.out_to_bottom);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

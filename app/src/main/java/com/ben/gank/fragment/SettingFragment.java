package com.ben.gank.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ben.gank.GankApplication;
import com.ben.gank.R;
import com.ben.gank.activity.base.AppSwipeBackActivity;
import com.ben.gank.adapter.SettingsAdapter;
import com.ben.gank.preferences.GeneralPrefs;
import com.ben.gank.utils.SystemUtils;

import butterknife.Bind;


public class SettingFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.list_view)
    ListView mListView;

    SettingsAdapter settingsAdapter;
    GeneralPrefs generalPrefs;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        generalPrefs = GankApplication.from(getContext()).getGeneralPrefs();
        settingsAdapter = new SettingsAdapter(getActivity());
        settingsAdapter.setOpenUrl(generalPrefs.getOpenUrl());
        mListView.setAdapter(settingsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == SettingsAdapter.ID_OPEN_URL) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.open_url).setItems(R.array.way_open_url, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            generalPrefs.setOpenUrl(GeneralPrefs.OPEN_URL_WEB_VIEW);
                            settingsAdapter.setOpenUrl(GeneralPrefs.OPEN_URL_WEB_VIEW);
                            break;
                        case 1:
                            generalPrefs.setOpenUrl(GeneralPrefs.OPEN_URL_BROWSER);
                            settingsAdapter.setOpenUrl(GeneralPrefs.OPEN_URL_BROWSER);
                            break;
                    }
                    settingsAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }).show();
        } else if (id == SettingsAdapter.ID_ABOUT) {

        } else if (id == SettingsAdapter.ID_GITHUB) {
            String url = "https://github.com/developerbenwu/Gank4Camp";
            SystemUtils.openUrlByBrowser(getContext(), url);
        } else if (id == SettingsAdapter.ID_CONTACT_US) {
            sendEmail();
        }
    }

    public void sendEmail() {
        String address = "benwu831@126.com";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + address));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        if (SystemUtils.isIntentAvailable(getContext(), intent)) {
            startActivity(intent);
        } else {
            showSnackbar("请安装邮件App或者直接发送邮件到:" + address);
        }

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
}

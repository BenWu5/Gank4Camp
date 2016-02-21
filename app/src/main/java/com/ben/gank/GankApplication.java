package com.ben.gank;

import android.app.Application;
import android.content.Context;

import com.ben.gank.preferences.GeneralPrefs;
import com.ben.gank.realm.DataService;
import com.ben.gank.realm.RealmDataService;


public class GankApplication extends Application {
    static GeneralPrefs generalPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        generalPrefs = new GeneralPrefs(this);
        if (!generalPrefs.isInitData()) {
            DataService dataService = new RealmDataService(this);
            dataService.addTypeList();
            generalPrefs.setInitData(true);
        }
    }

    public static GankApplication from(Context context) {
        return (GankApplication) context.getApplicationContext();
    }

    public GeneralPrefs getGeneralPrefs() {
        return generalPrefs;
    }

    public static int getOpenUrl(){
        return  generalPrefs.getOpenUrl();
    }
}

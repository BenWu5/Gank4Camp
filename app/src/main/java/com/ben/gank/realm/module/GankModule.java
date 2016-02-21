package com.ben.gank.realm.module;


import com.ben.gank.model.RealmBookmark;
import com.ben.gank.model.RealmImage;
import com.ben.gank.model.RealmType;

import io.realm.annotations.RealmModule;

/**
 * Created by Hui on 2016/2/12.
 */
// Create the module
@RealmModule(classes = { RealmType.class, RealmBookmark.class, RealmImage.class})
public class GankModule {
}

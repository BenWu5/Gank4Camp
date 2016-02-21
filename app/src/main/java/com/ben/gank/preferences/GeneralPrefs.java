/*
 *    Copyright 2015 TedXiong <xiong-wei@hotmail.com>
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
 *
 */

package com.ben.gank.preferences;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GeneralPrefs extends Prefs {

    @IntDef({OPEN_URL_WEB_VIEW, OPEN_URL_BROWSER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OPEN_URL {
    }

    public static final int OPEN_URL_WEB_VIEW = 0;
    public static final int OPEN_URL_BROWSER = 1;

    private static final String PREFIX = "general_";
    private static final String KEY_INIT = "key_init";
    private static final String KEY_OPEN_URL = "key_open_url";

    private boolean isInit;
    private int openUrl;

    public GeneralPrefs(Context context) {
        super(context);
        refresh();
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public void refresh() {
        isInit = getBoolean(KEY_INIT, false);
        openUrl = getInteger(KEY_OPEN_URL, OPEN_URL_WEB_VIEW);
    }

    public boolean isInitData() {
        return isInit;
    }

    public void setInitData(boolean isInit) {
        this.isInit = isInit;
        setBoolean(KEY_INIT, isInit);
    }

    public @GeneralPrefs.OPEN_URL int getOpenUrl() {
        return openUrl;
    }

    public void setOpenUrl(@OPEN_URL int openUrl) {
        this.openUrl = openUrl;
        setInteger(KEY_OPEN_URL, openUrl);
    }

}

package com.ben.gank.utils;


import com.ben.gank.Config;
import com.ben.gank.R;

/**
 * Created by Hui on 2016/2/3.
 */
public class IconUtils {
    public static final int getIconRes(String url, String type) {
        if (type.equals(Config.TYPE_VIDEO)) {
            return R.drawable.ic_type_video;
        } else if (url.startsWith("http://blog.csdn.net")) {
            return R.drawable.ic_type_csdn;
        } else if (url.startsWith("https://github.com")) {
            return R.drawable.ic_type_github;
        } else if (url.startsWith("http://finalshares.com")) {
            return R.drawable.ic_type_finalshares;
        } else if (url.startsWith("http://www.jianshu.com")) {
            return R.drawable.ic_type_jianshu;
        } else if (url.startsWith("https://www.zhihu.com")) {
            return R.drawable.ic_type_zhihu;
        } else {
            return R.drawable.ic_type_web;
        }
    }

}

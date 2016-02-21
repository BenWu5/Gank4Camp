package com.ben.gank.utils;


import com.ben.gank.Config;
import com.ben.gank.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hui on 2016/2/8.
 */
public class TypeUtils {
    public static Map<String,Integer> resStringMap = new HashMap<String,Integer>();
    static {
        resStringMap.put(Config.TYPE_ANDROID, R.string.android);
        resStringMap.put(Config.TYPE_IOS, R.string.ios);
        resStringMap.put(Config.TYPE_FRONT_END, R.string.front_end);
        resStringMap.put(Config.TYPE_RECOMMEND, R.string.recommend);
        resStringMap.put(Config.TYPE_VIDEO, R.string.video);
        resStringMap.put(Config.TYPE_GIRL, R.string.girl);
        resStringMap.put(Config.TYPE_RESOURCES, R.string.resources);

    }
    public static int getTypeString(String type){
        return resStringMap.get(type);
    }


}

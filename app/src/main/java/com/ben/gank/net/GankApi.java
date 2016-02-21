package com.ben.gank.net;


import com.ben.gank.Config;
import com.ben.gank.model.Data;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Hui on 2016/2/5.
 */
public class GankApi {
    public static final String ENDPOINT = Constants.GANK_SERVER_IP;

    /**
     * 每次加载条目
     */
    public static final int LOAD_LIMIT = 20;
    /**
     * 加载起始页面
     */

    public static GankApi instance;

    private final GankCloudService mWebService;

    public static GankApi getIns() {
        if (null == instance) {
            synchronized (GankApi.class) {
                if (null == instance) {
                    instance = new GankApi();
                }
            }
        }
        return instance;
    }

    public GankApi() {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mWebService = restAdapter.create(GankCloudService.class);
    }

    public interface GankCloudService {
        @GET("/api/data/福利/{limit}/{page}")
        Observable<Data> getBenefitsGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/api/data/Android/{limit}/{page}")
        Observable<Data> getAndroidGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/api/data/iOS/{limit}/{page}")
        Observable<Data> getIosGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/api/data/前端/{limit}/{page}")
        Observable<Data> getFrontEndGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/api/data/休息视频/{limit}/{page}")
        Observable<Data> getVidoeGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/api/data/拓展资源/{limit}/{page}")
        Observable<Data> getResourcesGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/api/data/瞎推荐/{limit}/{page}")
        Observable<Data> getRecommendGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );


        @GET("/api/data/all/{limit}/{page}")
        Observable<Data> getAllGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/api/day/{year}/{month}/{day}")
        Observable<Data> getGoodsByDay(
                @Path("year") int year,
                @Path("month") int month,
                @Path("day") int day
        );
    }

    public Observable<Data> getCommonGoods(String type, int limit, int page) {
//        return mWebService.getGoods(type, limit, page);
        if (Config.TYPE_ANDROID.equalsIgnoreCase(type)) {
            return mWebService.getAndroidGoods(limit, page);
        } else if (Config.TYPE_IOS.equalsIgnoreCase(type)) {
            return mWebService.getIosGoods(limit, page);
        } else if (Config.TYPE_FRONT_END.equals(type)) {
            return mWebService.getFrontEndGoods(limit, page);
        } else if (Config.TYPE_RECOMMEND.equals(type)) {
            return mWebService.getRecommendGoods(limit, page);
        } else if (Config.TYPE_VIDEO.equals(type)) {
            return mWebService.getVidoeGoods(limit, page);
        } else if (Config.TYPE_GIRL.equals(type)) {
            return mWebService.getBenefitsGoods(limit, page);
        } else if (Config.TYPE_RESOURCES.equals(type)) {
            return mWebService.getResourcesGoods(limit, page);
        } else {
            return mWebService.getAndroidGoods(limit, page);
        }
    }

}

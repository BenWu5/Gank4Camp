package com.ben.gank.realm;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import com.ben.gank.Config;
import com.ben.gank.model.Bookmark;
import com.ben.gank.model.Image;
import com.ben.gank.model.RealmBookmark;
import com.ben.gank.model.RealmImage;
import com.ben.gank.model.RealmType;
import com.ben.gank.model.Result;
import com.ben.gank.model.Type;
import com.ben.gank.realm.migration.GankMigration;
import com.ben.gank.realm.module.GankModule;
import com.ben.gank.realm.rx.RealmObservable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Hui on 2016/2/12.
 */
public class RealmDataService implements DataService {

    private final Context context;
    private final RealmConfiguration configuration;

    private static RealmConfiguration defaultConfiguration;

    private final OkHttpClient client = new OkHttpClient();

    public RealmDataService(Context context) {
        this(context, null);
    }

    public RealmDataService(Context context, RealmConfiguration configuration) {
        this.context = context;
        if (configuration == null) {
            this.configuration = getDefaultConfiguration(context);
        } else {
            this.configuration = configuration;
        }
    }

    public RealmConfiguration getDefaultConfiguration(Context context) {
        if (defaultConfiguration == null) {
            defaultConfiguration = new RealmConfiguration.Builder(context)
                    .name("Gank.realm")
                    .schemaVersion(0)
                    .setModules(new GankModule())
                    .migration(new GankMigration())
                    .build();
        }
        return defaultConfiguration;
    }

    @Override
    public Observable<List<Type>> getVisibilityTypeList() {
        return RealmObservable.results(context, defaultConfiguration, new Func1<Realm, RealmResults<RealmType>>() {
            @Override
            public RealmResults<RealmType> call(Realm realm) {
                return realm.where(RealmType.class).equalTo("visibility", true).findAllSorted("sort", Sort.ASCENDING);
            }
        }).map(new Func1<RealmResults<RealmType>, List<Type>>() {
            @Override
            public List<Type> call(RealmResults<RealmType> realmResults) {
                final List<Type> typeList = new ArrayList<Type>(realmResults.size());
                for (RealmType realmType : realmResults) {
                    typeList.add(RealmType2Type(realmType));
                }
                return typeList;
            }
        });
    }

    @Override
    public Observable<List<Type>> getAllTypeList() {
        return RealmObservable.results(context, defaultConfiguration,new Func1<Realm, RealmResults<RealmType>>() {
            @Override
            public RealmResults<RealmType> call(Realm realm) {
                return realm.where(RealmType.class).findAllSorted("sort", Sort.ASCENDING);
            }
        }).map(new Func1<RealmResults<RealmType>, List<Type>>() {
            @Override
            public List<Type> call(RealmResults<RealmType> realmResults) {
                final List<Type> typeList = new ArrayList<Type>(realmResults.size());
                for (RealmType realmType : realmResults) {
                    typeList.add(RealmType2Type(realmType));
                }
                return typeList;
            }
        });
    }

    @Override
    public Observable<Boolean> updateTypeList(final List<Type> list) {
        return RealmObservable.resultsBoolean(context,defaultConfiguration, new Func1<Realm, Boolean>() {
            @Override
            public Boolean call(Realm realm) {
                for (Type type : list) {
                    realm.copyToRealmOrUpdate(Type2RealmType(type));
                }
                return Boolean.TRUE;
            }
        });
    }

    @Override
    public void addTypeList() {
        Realm realm = Realm.getInstance(defaultConfiguration);
        realm.beginTransaction();
        for (int i = 0; i < Config.TYPES.length; i++) {
            RealmType realmType = realm.createObject(RealmType.class);
            realmType.setTitle(Config.TYPES[i]);
            realmType.setSort(i);
            realmType.setVisibility(true);
            realm.copyToRealm(realmType);
        }
        realm.commitTransaction();
    }

    @Override
    public Observable<Bookmark> addBookmark(final Bookmark bookmark) {
        return RealmObservable.object(context,defaultConfiguration, new Func1<Realm, RealmBookmark>() {
            @Override
            public RealmBookmark call(Realm realm) {
                RealmBookmark realmBookmark = Bookmark2RealmBookmark(bookmark);
                return realm.copyToRealmOrUpdate(realmBookmark);
            }
        }).map(new Func1<RealmBookmark, Bookmark>() {
            @Override
            public Bookmark call(RealmBookmark realmBookmark) {
                return RealmBookmark2Bookmark(realmBookmark);
            }
        });
    }

    @Override
    public Observable<Bookmark> removeBookmark(final String id) {
        return RealmObservable.object(context,defaultConfiguration, new Func1<Realm, RealmBookmark>() {
            @Override
            public RealmBookmark call(Realm realm) {
                RealmBookmark results = realm.where(RealmBookmark.class).equalTo("objectId", id).findFirst();
                results.removeFromRealm();
                return results;
            }
        }).map(new Func1<RealmBookmark, Bookmark>() {
            @Override
            public Bookmark call(RealmBookmark realmBookmark) {
                return null;
            }
        });
    }

    @Override
    public Observable<Bookmark> findBookmarkById(final String id) {
        return RealmObservable.object(context,defaultConfiguration, new Func1<Realm, RealmBookmark>() {
            @Override
            public RealmBookmark call(Realm realm) {
                return realm.where(RealmBookmark.class).equalTo("objectId", id).findFirst();
            }
        }).map(new Func1<RealmBookmark, Bookmark>() {
            @Override
            public Bookmark call(RealmBookmark realmBookmark) {
                if (realmBookmark == null) {
                    return null;
                } else {
                    return RealmBookmark2Bookmark(realmBookmark);
                }
            }
        });
    }

    @Override
    public Observable<List<Bookmark>> getBookmarkList() {
        return getBookmarkList(null);
    }

    @Override
    public Observable<List<Bookmark>> getBookmarkList(final String type) {
        return RealmObservable.results(context,defaultConfiguration, new Func1<Realm, RealmResults<RealmBookmark>>() {
            @Override
            public RealmResults<RealmBookmark> call(Realm realm) {
                RealmQuery<RealmBookmark> query = realm.where(RealmBookmark.class);
                if (!TextUtils.isEmpty(type)) {
                    query.equalTo("type", type);
                }
                return query.findAllSorted("collectionAt", Sort.DESCENDING);
            }
        }).map(new Func1<RealmResults<RealmBookmark>, List<Bookmark>>() {
            @Override
            public List<Bookmark> call(RealmResults<RealmBookmark> realmResults) {
                final List<Bookmark> bookmarkList = new ArrayList<Bookmark>(realmResults.size());
                for (RealmBookmark realmBookmark : realmResults) {
                    bookmarkList.add(RealmBookmark2Bookmark(realmBookmark));
                }
                return bookmarkList;
            }
        });
    }

    @Override
    public Observable<List<Image>> addImageList(final List<Result> results) {
        return RealmObservable.resultList(context,defaultConfiguration, new Func1<Realm, List<RealmImage>>() {
            @Override
            public List<RealmImage> call(Realm realm) {
                if (results != null && !realm.isEmpty()) {
                    List<RealmImage> imageList = new ArrayList<>();
                    for (Result result : results) {
                        RealmImage realmImage = realm.where(RealmImage.class).equalTo("objectId", result.getObjectId())
                                .notEqualTo("width",0).findFirst();
                        if (realmImage == null) {
                            realmImage = results2RealmImage(result);
                            loadImageForSize(realmImage);
                            realm.copyToRealm(realmImage);
                        }
                        imageList.add(realmImage);
                    }
                    return imageList;
                } else {
                    return null;
                }
            }
        }).map(new Func1<List<RealmImage>, List<Image>>() {
            @Override
            public List<Image> call(List<RealmImage> realmImages) {
                if (realmImages == null && realmImages.isEmpty()) {
                    return null;
                } else {
                    List imageList = new ArrayList(realmImages.size());
                    for (RealmImage realImage : realmImages) {
                        imageList.add(realmImage2Image(realImage));
                    }
                    return imageList;
                }
            }
        });
    }


    /**
     * 预解码图片并将抓到的图片尺寸保存至数据库
     *
     * @return 是否保存成功
     */
    private boolean loadImageForSize(RealmImage image) {
        try {
            Point size = new Point();
            loadImageForSize(image.getUrl(), size);
            image.setHeight(size.y);
            image.setWidth(size.x);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /***
     * 加载图片内容计算图片大小
     *
     * @param url
     * @param measured
     * @throws IOException
     */
    public void loadImageForSize(String url, Point measured) throws IOException {
        Response response = client.newCall(new Request.Builder().url(url).build()).execute();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(response.body().byteStream(), null, options);
        measured.x = options.outWidth;
        measured.y = options.outHeight;
    }

    public static final Image realmImage2Image(RealmImage realmImage) {
        Image image = new Image();
        image.setWho(realmImage.getWho());
        image.setPublishedAt(realmImage.getPublishedAt());
        image.setHeight(realmImage.getHeight());
        image.setWidth(realmImage.getWidth());
        image.setDesc(realmImage.getDesc());
        image.setType(realmImage.getType());
        image.setUrl(realmImage.getUrl());
        image.setUsed(realmImage.isUsed());
        image.setObjectId(realmImage.getObjectId());
        image.setCreatedAt(realmImage.getCreatedAt());
        image.setUpdatedAt(realmImage.getUpdatedAt());
        return image;
    }

    public static final RealmImage results2RealmImage(Result result) {
        RealmImage image = new RealmImage();
        image.setWho(result.getWho());
        image.setPublishedAt(result.getPublishedAt());
        image.setDesc(result.getDesc());
        image.setType(result.getType());
        image.setUrl(result.getUrl());
        image.setUsed(result.isUsed());
        image.setObjectId(result.getObjectId());
        image.setCreatedAt(result.getCreatedAt());
        image.setUpdatedAt(result.getUpdatedAt());
        return image;
    }

    public static final Type RealmType2Type(RealmType realmType) {
        return new Type(realmType.getTitle(), realmType.getSort(), realmType.isVisibility());
    }

    public static final RealmType Type2RealmType(Type type) {
        return new RealmType(type.getTitle(), type.getSort(), type.isVisibility());
    }

    public static final Bookmark RealmBookmark2Bookmark(RealmBookmark realmBookmark) {
        return new Bookmark(realmBookmark.getObjectId(), realmBookmark.getCollectionAt(), realmBookmark.getDesc(), realmBookmark.getType(), realmBookmark.getUrl(), realmBookmark.getWho());
    }

    public static final RealmBookmark Bookmark2RealmBookmark(Bookmark bookmark) {
        return new RealmBookmark(bookmark.getObjectId(), bookmark.getCollectionAt(), bookmark.getDesc(), bookmark.getType(), bookmark.getUrl(), bookmark.getWho());
    }
}

package com.ben.gank.realm.rx;

import android.content.Context;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

public final class RealmObservable {
    private RealmObservable() {
    }

    public static <T extends RealmObject> Observable<T> object(Context context, final Func1<Realm, T> function) {
        return Observable.create(new OnSubscribeRealm<T>(context) {
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<T> object(Context context, RealmConfiguration configuration, final Func1<Realm, T> function) {
        return Observable.create(new OnSubscribeRealm<T>(context, configuration) {
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<RealmList<T>> list(Context context, final Func1<Realm, RealmList<T>> function) {
        return Observable.create(new OnSubscribeRealm<RealmList<T>>(context) {
            @Override
            public RealmList<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<RealmList<T>> list(Context context,RealmConfiguration configuration, final Func1<Realm, RealmList<T>> function) {
        return Observable.create(new OnSubscribeRealm<RealmList<T>>(context, configuration) {
            @Override
            public RealmList<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<RealmResults<T>> results(Context context, final Func1<Realm, RealmResults<T>> function) {
        return Observable.create(new OnSubscribeRealm<RealmResults<T>>(context) {
            @Override
            public RealmResults<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<RealmResults<T>> results(Context context,RealmConfiguration configuration, final Func1<Realm, RealmResults<T>> function) {
        return Observable.create(new OnSubscribeRealm<RealmResults<T>>(context, configuration) {
            @Override
            public RealmResults<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static  Observable<Boolean> resultsBoolean(Context context,RealmConfiguration configuration, final Func1<Realm, Boolean> function) {
        return Observable.create(new OnSubscribeRealm<Boolean>(context, configuration) {
            @Override
            public Boolean get(Realm realm) {
                return function.call(realm);
            }
        });
    }
    public static  Observable<Boolean> resultsBoolean(Context context, final Func1<Realm, Boolean> function) {
        return Observable.create(new OnSubscribeRealm<Boolean>(context) {
            @Override
            public Boolean get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<List<T>> resultList(Context context, final Func1<Realm, List<T>> function) {
        return Observable.create(new OnSubscribeRealm<List<T>>(context) {
            @Override
            public List<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }
    public static <T extends RealmObject> Observable<List<T>> resultList(Context context,RealmConfiguration configuration, final Func1<Realm, List<T>> function) {
        return Observable.create(new OnSubscribeRealm<List<T>>(context,configuration) {
            @Override
            public List<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }

}

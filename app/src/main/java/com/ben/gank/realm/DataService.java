package com.ben.gank.realm;


import com.ben.gank.model.Bookmark;
import com.ben.gank.model.Image;
import com.ben.gank.model.Result;
import com.ben.gank.model.Type;

import java.util.List;

import rx.Observable;

public interface DataService {

    Observable<List<Type>> getVisibilityTypeList();

    Observable<List<Type>> getAllTypeList();

    Observable<Boolean> updateTypeList(List<Type> list);

    void addTypeList();


    Observable<Bookmark> addBookmark(Bookmark bookmark);

    Observable<Bookmark> removeBookmark(String id);

    Observable<Bookmark> findBookmarkById(String id);

    Observable<List<Bookmark>> getBookmarkList();

    Observable<List<Bookmark>> getBookmarkList(String type);

    Observable<List<Image>> addImageList(List<Result> results);

}

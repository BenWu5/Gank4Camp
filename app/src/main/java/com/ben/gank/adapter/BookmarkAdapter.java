package com.ben.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ben.gank.R;
import com.ben.gank.activity.WebActivity;
import com.ben.gank.model.Bookmark;
import com.ben.gank.utils.IconUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hui on 2016/1/26.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Bookmark> mDatas;
    private Context mContext;

    public BookmarkAdapter(Context context) {
        this.mContext = context;
        mDatas = new ArrayList<>();
    }

    @Override
    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BookmarkViewHolder holder = new BookmarkViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_list, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(BookmarkViewHolder holder, int position) {
        Bookmark bookmark = mDatas.get(position);
        holder.setBookmark(bookmark);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    class BookmarkViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView titleView;
        final TextView whoView;
        final View rootView;
        private Bookmark bookmark;

        public BookmarkViewHolder(View view) {
            super(view);
            rootView = view;
            titleView = (TextView) view.findViewById(R.id.title);
            whoView = (TextView) view.findViewById(R.id.who);
            iconView = (ImageView) view.findViewById(R.id.icon);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startWebActivity(mContext, bookmark);
                }
            });
        }

        public void setBookmark(Bookmark bookmark) {
            this.bookmark = bookmark;
            titleView.setText(bookmark.getDesc());
            whoView.setText(bookmark.getWho());
            iconView.setImageResource(IconUtils.getIconRes(bookmark.getUrl(), bookmark.getType()));

        }
    }

    public void addData(List<Bookmark> datas) {
        this.mDatas.addAll(datas);
    }

    public void setData(List<Bookmark> datas) {
        this.mDatas = datas;
    }

}


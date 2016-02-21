package com.ben.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ben.gank.GankApplication;
import com.ben.gank.R;
import com.ben.gank.activity.WebActivity;
import com.ben.gank.model.Result;
import com.ben.gank.preferences.GeneralPrefs;
import com.ben.gank.utils.IconUtils;
import com.ben.gank.utils.SystemUtils;
import com.ben.gank.view.SectionsDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hui on 2016/1/26.
 */
public class GankAdapter extends RecyclerView.Adapter<GankAdapter.FunnyViewHolder> implements SectionsDecoration.Adapter<GankAdapter.HeaderViewHolder> {

    private List<Result> mDatas;
    private Context mContext;

    public GankAdapter(Context context) {
        this.mContext = context;
        mDatas = new ArrayList<>();
    }

    @Override
    public FunnyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FunnyViewHolder holder = new FunnyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_list, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(FunnyViewHolder holder, int position) {
        Result result = mDatas.get(position);
        holder.setResult(result);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public long getHeaderId(int position) {
        return mDatas.get(position).getPublishedDataTime().withTimeAtStartOfDay().getMillis();
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        viewHolder.titleTextView.setText(DateUtils.formatDateTime(mContext, mDatas.get(position).getPublishedDataTime().getMillis(), DateUtils.FORMAT_SHOW_DATE));
    }

    class FunnyViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView titleView;
        final TextView whoView;
        final View rootView;
        private Result result;

        public FunnyViewHolder(View view) {
            super(view);
            rootView = view;
            titleView = (TextView) view.findViewById(R.id.title);
            whoView = (TextView) view.findViewById(R.id.who);
            iconView = (ImageView) view.findViewById(R.id.icon);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GankApplication.getOpenUrl()== GeneralPrefs.OPEN_URL_WEB_VIEW){
                        WebActivity.startWebActivity(mContext, result);
                    }else{
                        SystemUtils.openUrlByBrowser(mContext, result.getUrl());
                    }

                }
            });
        }

        public void setResult(Result result) {
            this.result = result;
            titleView.setText(result.getDesc());
            whoView.setText(result.getWho());
            iconView.setImageResource(IconUtils.getIconRes(result.getUrl(), result.getType()));

        }
    }

    public void addData(List<Result> datas) {
        this.mDatas.addAll(datas);
    }

    public void setData(List<Result> datas) {
        this.mDatas = datas;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }
}


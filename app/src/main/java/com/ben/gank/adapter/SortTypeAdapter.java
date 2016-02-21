/*
 *    Copyright (C) 2015 Haruki Hasegawa
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
 */

package com.ben.gank.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ben.gank.R;
import com.ben.gank.model.Type;
import com.ben.gank.utils.TypeUtils;
import com.ben.gank.utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SortTypeAdapter extends RecyclerView.Adapter<SortTypeAdapter.MyViewHolder>
        implements DraggableItemAdapter<SortTypeAdapter.MyViewHolder> {
    private static final String TAG = "MyDragSectionAdapter";

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private static final int ITEM_VIEW_TYPE_SECTION_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_SECTION_ITEM = 1;

    private List<Item> visibilityList;
    private List<Item> inVisibilityList;

    public static Item TypeItem;
    public static Item MoreItem;

    private class Item {

        boolean isSectionHeader;
        String title;
        Type type;

        public Item(Type type) {
            this.isSectionHeader = false;
            this.title = type.getTitle();
            this.type = type;
        }

        public Item(String headerTitle) {
            this.isSectionHeader = true;
            this.title = headerTitle;
            this.type = null;
        }

        public boolean isSectionHeader() {
            return isSectionHeader;
        }

        public void setIsSectionHeader(boolean isSectionHeader) {
            this.isSectionHeader = isSectionHeader;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public LinearLayout mContainer;
        public View mDragHandle;
        public CheckBox mCheckBox;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (LinearLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.image);
            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }
    }

    public SortTypeAdapter(Context context) {
        TypeItem = new Item(context.getString(R.string.type));
        MoreItem = new Item(context.getString(R.string.more));
        setHasStableIds(true);
    }
    public boolean isVisibilityListEmpty(){
        if(visibilityList==null||visibilityList.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    public void typeList2ItemList(List<Type> datas) {
        visibilityList = new ArrayList<>();
        inVisibilityList = new ArrayList<>();
        visibilityList.add(TypeItem);
        inVisibilityList.add(MoreItem);

        for (int i = 0; i < datas.size(); i++) {
            Type type = datas.get(i);
            if (type.isVisibility()) {
                visibilityList.add(new Item(type));
            } else {
                inVisibilityList.add(new Item(type));
            }
        }
        if (visibilityList.size() == 1) {
            visibilityList.clear();
        }
        if (inVisibilityList.size() == 1) {
            inVisibilityList.clear();
        }
    }

    public List<Type> itemList2TypeList() {
        List<Type> categories = new ArrayList<>();
        Type tempCategories = null;
        int position = 0;
        if(visibilityList!=null&&visibilityList.size()>1){
            for(int i = 1;i<visibilityList.size();i++){
                tempCategories = visibilityList.get(i).getType();
                categories.add(new Type(tempCategories.getTitle(),position,true));
                position++;
            }
        }

        if(inVisibilityList!=null&&inVisibilityList.size()>1){
            for(int i = 1;i<inVisibilityList.size();i++){
                tempCategories = inVisibilityList.get(i).getType();
                categories.add(new Type(tempCategories.getTitle(),position,false));
                position++;
            }
        }
        return categories;
    }





    @Override
    public long getItemId(int position) {
        return getItemByPosition(position).hashCode();
    }

    public Item getItemByPosition(int position) {
        if (position > visibilityList.size() - 1) {
            return inVisibilityList.get(position - visibilityList.size());
        } else {
            return visibilityList.get(position);
        }
    }

    public void setItemByPosition(int position,Item item){
        if (position > visibilityList.size() - 1) {
             inVisibilityList.set(position - visibilityList.size(), item);
        } else {
             visibilityList.set(position, item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemByPosition(position).isSectionHeader() ? ITEM_VIEW_TYPE_SECTION_HEADER : ITEM_VIEW_TYPE_SECTION_ITEM;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final View v;
        switch (viewType) {
            case ITEM_VIEW_TYPE_SECTION_HEADER:
                v = inflater.inflate(R.layout.list_section_header, parent, false);
                break;
            case ITEM_VIEW_TYPE_SECTION_ITEM:
                v = inflater.inflate(R.layout.item_sort, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected viewType (= " + viewType + ")");
        }

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ITEM_VIEW_TYPE_SECTION_HEADER:
                onBindSectionHeaderViewHolder(holder, position);
                break;
            case ITEM_VIEW_TYPE_SECTION_ITEM:
                onBindSectionItemViewHolder(holder, position);
                break;
        }
    }

    private void onBindSectionHeaderViewHolder(MyViewHolder holder, int position) {
        final Item item = getItemByPosition(position);

        // set text
        holder.mTextView.setText(item.getTitle());
    }

    private void onBindSectionItemViewHolder(MyViewHolder holder, final int position) {
        final Item item = getItemByPosition(position);

        // set text
        holder.mCheckBox.setText(TypeUtils.getTypeString(item.getTitle()));
        holder.mCheckBox.setOnCheckedChangeListener(null);
        holder.mCheckBox.setChecked(item.getType().isVisibility());
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.getType().setVisibility(isChecked);
                Item header;
                List<Item> addedList;
                List<Item> removeList;
                if (isChecked) {
                    header = TypeItem;
                    addedList = visibilityList;
                    removeList = inVisibilityList;
                } else {
                    header = MoreItem;
                    addedList = inVisibilityList;
                    removeList = visibilityList;
                }
                if (addedList.isEmpty()) {
                    addedList.add(header);
                }
                addedList.add(item);
                removeList.remove(item);
                if (removeList.size() == 1) {
                    removeList.clear();
                }

                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return inVisibilityList==null||inVisibilityList==null?0:inVisibilityList.size() + visibilityList.size();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }
        if(fromPosition<visibilityList.size()){
            Item item = visibilityList.remove(fromPosition);
            visibilityList.add(toPosition,item);
        }else {
            Item item = inVisibilityList.remove(fromPosition-visibilityList.size());
            inVisibilityList.add(toPosition-visibilityList.size(),item);
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left

        // return false if the item is a section header
        if (holder.getItemViewType() != ITEM_VIEW_TYPE_SECTION_ITEM) {
            return false;
        }

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        final int start = findFirstSectionItem(position);
        final int end = findLastSectionItem(position);

        return new ItemDraggableRange(start, end);
    }

    private int findFirstSectionItem(int position) {
        Item item = getItemByPosition(position);

        if (item.isSectionHeader()) {
            throw new IllegalStateException("section item is expected");
        }

        while (position > 0) {
            Item prevItem = getItemByPosition(position - 1);
            if (prevItem.isSectionHeader()) {
                break;
            }

            position -= 1;
        }

        return position;
    }

    private int findLastSectionItem(int position) {
        Item item = getItemByPosition(position);

        if (item.isSectionHeader()) {
            throw new IllegalStateException("section item is expected");
        }

        final int lastIndex = getItemCount() - 1;

        while (position < lastIndex) {
            Item nextItem = getItemByPosition(position + 1);

            if (nextItem.isSectionHeader()) {
                break;
            }

            position += 1;
        }

        return position;
    }
}

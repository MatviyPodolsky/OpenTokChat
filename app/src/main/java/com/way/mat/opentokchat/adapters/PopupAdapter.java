package com.way.mat.opentokchat.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.items.PopupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matviy on 08.09.16.
 */
public class PopupAdapter implements ListAdapter {

    List<PopupItem> items;
    LayoutInflater inflater;

    public PopupAdapter(Context context, List<PopupItem> items) {
        this.items = new ArrayList<>();
        this.items.addAll(items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return items != null ? items.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final View v = inflater.inflate(R.layout.popup_layout, null);
        final PopupItem item = (PopupItem) getItem(i);
        ((ImageView)v.findViewById(R.id.img_popup)).setImageResource(item.getIcon());
        ((TextView)v.findViewById(R.id.text)).setText(item.getTitle());
        return v;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}

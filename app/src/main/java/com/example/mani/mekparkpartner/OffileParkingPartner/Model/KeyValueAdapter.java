package com.example.mani.mekparkpartner.OffileParkingPartner.Model;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.mani.mekparkpartner.R;

import java.util.List;

public class KeyValueAdapter extends ArrayAdapter<KeyValueClass> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<KeyValueClass> items;
    private final int mResource;

    public KeyValueAdapter(@NonNull Context context, @LayoutRes int resource,
                           @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){

        final View view = mInflater.inflate(mResource, parent, false);

        TextView textView =  view.findViewById(R.id.text_view);

        KeyValueClass keyValueClass = items.get(position);

        textView.setText(keyValueClass.getValue());

        return view;
    }
}


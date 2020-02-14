package com.moulinette.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.moulinette.R;
import com.moulinette.models.leftmenu.DataModel_;

import java.util.ArrayList;


public class LefeMenuAdapter extends ArrayAdapter<DataModel_> {

    Context mContext;
    int layoutResourceId;
    ArrayList<DataModel_> data = new ArrayList<>();
    private Typeface content;

    public LefeMenuAdapter(Context mContext, int layoutResourceId, ArrayList<DataModel_> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);
        textViewName.setTypeface(content);
        DataModel_ folder = data.get(position);

        imageViewIcon.setImageResource(folder.getImage());
        textViewName.setText(folder.getName());

        return listItem;
    }
}


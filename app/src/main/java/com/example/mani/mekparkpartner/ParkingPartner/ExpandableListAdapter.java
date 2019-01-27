package com.example.mani.mekparkpartner.ParkingPartner;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.mani.mekparkpartner.R;

import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<MenuModel> listDataHeader;
    private HashMap<MenuModel, List<MenuModel>> listDataChild;

    public ExpandableListAdapter(Context context, List<MenuModel> listDataHeader,
                                 HashMap<MenuModel, List<MenuModel>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild  = listChildData;
    }

    @Override
    public MenuModel getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).getMenuName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_child,null);
        }

        Typeface typefaceGBR = Typeface.createFromAsset(context.getAssets(),"fonts/gotham_book_regular.otf");

        TextView txtListChild = convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        txtListChild.setTypeface(typefaceGBR);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (this.listDataChild.get(this.listDataHeader.get(groupPosition)) == null)
            return 0;
        else
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
    }

    @Override
    public MenuModel getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        MenuModel menuModel = getGroup(groupPosition);

        String headerTitle = getGroup(groupPosition).getMenuName();
        int leftDrawalbleId = getGroup(groupPosition).getIcon_id();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_header,null);
        }

        Typeface typefaceGMR = Typeface.createFromAsset(context.getAssets(),"fonts/gotham_medium_regular.ttf");

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(typefaceGMR);

        lblListHeader.setText(headerTitle);

        if(menuModel.isHasChildren()) {
            lblListHeader.setCompoundDrawablesWithIntrinsicBounds(leftDrawalbleId,
                    0, isExpanded ? R.drawable.arrow_down : R.drawable.arrow_right, 0);
        }
        else {
            lblListHeader.setCompoundDrawablesWithIntrinsicBounds(leftDrawalbleId,
                    0,0, 0);

        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
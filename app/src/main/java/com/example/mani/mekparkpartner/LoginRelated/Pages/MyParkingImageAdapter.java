package com.example.mani.mekparkpartner.LoginRelated.Pages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mani.mekparkpartner.R;

import java.util.List;

public class MyParkingImageAdapter extends RecyclerView.Adapter<MyParkingImageAdapter.MyParkingViewHolder> {

    private Context mCtx;
    List<MyParkingImage> myParkingImageList;

    public MyParkingImageAdapter(Context mCtx, List<MyParkingImage> myParkingImageList) {
        this.mCtx = mCtx;
        this.myParkingImageList = myParkingImageList;
    }

    @NonNull
    @Override
    public MyParkingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycle_view_ny_parking_images,viewGroup,false);
        return new MyParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyParkingViewHolder myParkingViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return myParkingImageList.size();
    }

    public class MyParkingViewHolder extends RecyclerView.ViewHolder {
        public MyParkingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

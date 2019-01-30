package com.example.mani.mekparkpartner.CommonForAllPartner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.SERVICE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;

public class SimplemageAdapter extends RecyclerView.Adapter<SimplemageAdapter.MyParkingViewHolder> {

    private final String TAG = "SimpleImageAdapter";
    private Context mCtx;
    List<SimpleImage> mImageList;

    private ProgressDialog mProgressDialog;

    public SimplemageAdapter(Context mCtx, List<SimpleImage> imageList) {
        this.mCtx = mCtx;
        this.mImageList = imageList;

        mProgressDialog = new ProgressDialog(mCtx);
    }

    @NonNull
    @Override
    public MyParkingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycle_view_ny_parking_images,viewGroup,false);
        return new MyParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyParkingViewHolder holder, int i) {

        final SimpleImage simpleImage = mImageList.get(i);

        String imagePath = SERVICE_IMAGE_PATH +  simpleImage.getImageName();

        Glide.with(mCtx)
                .load(imagePath)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.imageProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.imageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleleImage(simpleImage.getImage_id());
            }
        });


    }


    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class MyParkingViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ProgressBar imageProgressBar;
        ImageView deleteImage;

        public MyParkingViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView        = itemView.findViewById(R.id.imageView);
            imageProgressBar = itemView.findViewById(R.id.image_progress_bar);

            deleteImage = itemView.findViewById(R.id.delete);
        }
    }

    private void deleleImage(final int image_id) {

        mProgressDialog.show();

        String SEND_URL = BASE_URL + "delete_service_image.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG,response);
                mProgressDialog.dismiss();

                ((Activity)mCtx).recreate();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e(TAG,error.toString());
                Toast.makeText(mCtx,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.put("image_id", String.valueOf(image_id));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(mCtx).addToRequestQueue(stringRequest);

    }

}

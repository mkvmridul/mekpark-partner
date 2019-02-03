package com.example.mani.mekparkpartner;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.LOCATION_NOT_FOUND;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getDeviceLocation;


public class AddressDialog extends DialogFragment implements OnMapReadyCallback {

    GoogleMap mMap;
    private final String TAG = "AddressDialog";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float MARKER_ZOOM = 18f;

    private Boolean mLocationPermissionsGranted = false;
    private LatLng mSelectedLatlng;

    private View mRootView;

    //Widget
    EditText et_comAdd,et_landmark,et_locality,et_city;
    private ProgressBar mProgressBar;

    public AddressDialog() {}

    public interface AddressDialogListenr {
        void getAddressViaListener (LatLng selectedLatlng,String completeAddress, String landmark,String locality, String city);
    }

    public AddressDialogListenr mAddressDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Log.e(TAG, "called : onCreateDialog");

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "called : onCreateView");
        mRootView = inflater.inflate(R.layout.address_page, container, false);

        mRootView.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });

        mProgressBar = mRootView.findViewById(R.id.progress_horizontal);

        et_comAdd   =  mRootView.findViewById(R.id.complete_address);
        et_landmark =  mRootView.findViewById(R.id.landmark);
        et_locality =  mRootView.findViewById(R.id.locality);
        et_city     =  mRootView.findViewById(R.id.city);

        getLocationPermission();
        return mRootView;
    }

    private void initMap() {
        Log.e(TAG, "called : initMap");
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e(TAG, "called : omMapReady");
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if(mLocationPermissionsGranted){
            animateToCurrentLocation();
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = mMap.getCameraPosition().target;
                if(latLng!=null)
                    setAddress(latLng);
                else {
                    Log.e(TAG, "OnCameraIdelListener latlng is null");

                }

            }
        });


        clickListener();

    }

    private void clickListener() {

        mRootView.findViewById(R.id.gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateToCurrentLocation();
            }
        });

        mRootView.findViewById(R.id.use_marker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateToCurrentLocation();
            }
        });

        mRootView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSelectedLatlng==null){
                    Toast.makeText(getActivity(),"Current Latlng is null",Toast.LENGTH_SHORT).show();
                    return;
                }

                String cAdd     = et_comAdd.getText().toString().trim();
                String landmark = et_landmark.getText().toString().trim();
                String locality = et_locality.getText().toString().trim();
                String city     = et_city.getText().toString().trim();

                if(cAdd.equals("") || landmark.equals("") || locality.equals("") || city.equals("")||cAdd.equals(LOCATION_NOT_FOUND)){
                    Toast.makeText(getActivity(),"all fields are required",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e(TAG,"mCurrent : "+mSelectedLatlng+" cADD : "+cAdd+" locality : "+locality+" landmark :"+landmark+" city +"+city);
                mAddressDialogListener.getAddressViaListener(mSelectedLatlng,cAdd,landmark,locality,city);
                getDialog().dismiss();

            }
        });

    }


    private void animateToCurrentLocation() {

        mMap.clear();

        try {

            mSelectedLatlng = getDeviceLocation(getActivity());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatlng,MARKER_ZOOM));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getLocationPermission() {

        Log.e(TAG, "getLocationPermission");

        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG,"called : onDestroyView");
        super.onDestroyView();
        assert getFragmentManager() != null;
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    private void setAddress(LatLng latLng) {

        Log.e(TAG,"called : setAddress");
        mProgressBar.setVisibility(View.VISIBLE);

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses == null){

            Log.e(TAG, "Address is null");
            et_comAdd.setText(LOCATION_NOT_FOUND);
            mProgressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if(addresses.size() == 0){
            Log.e(TAG, "Address size is 0");
            et_comAdd.setText(LOCATION_NOT_FOUND);
            mProgressBar.setVisibility(View.INVISIBLE);
            return;
        }

        String address     = addresses.get(0).getAddressLine(0);
        String city        = addresses.get(0).getLocality();
        String state       = addresses.get(0).getAdminArea();
        String country     = addresses.get(0).getCountryName();
        String postalCode  = addresses.get(0).getPostalCode();
        String knownName   = addresses.get(0).getFeatureName();

        mSelectedLatlng = latLng;

        et_comAdd.setText(address);
        et_locality.setText(knownName);
        et_city.setText(city);

        mProgressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mAddressDialogListener = (AddressDialogListenr) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG,"onAttach: ClassCastException: "+e.getMessage());
        }
    }

}

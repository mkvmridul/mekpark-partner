package com.example.mani.mekparkpartner.ParkingPartner.Listener;

import com.example.mani.mekparkpartner.ParkingPartner.Booking;

import java.util.List;

public interface MyListener {

    void setData();
    List<Booking> fetchData(int status);
}

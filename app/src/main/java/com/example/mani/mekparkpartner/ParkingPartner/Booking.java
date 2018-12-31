package com.example.mani.mekparkpartner.ParkingPartner;

import java.io.Serializable;

public class Booking implements Serializable {

    private int bookingId;
    private int cusId;

    private String bookingTime;
    private String parkInTime;
    private String parkOutTime;
    private String duration;
    private boolean payCon;

    private String fare;
    private int status;
    private int pin;

    private String brand;
    private String model;
    private String licencePlateNo;
    private String vehicleImage;

    private String cusName;
    private String cusPhone;

    public Booking(int bookingId, int cusId, String bookingTime, String parkInTime, String parkOut, String duration, boolean payCon,
                   String fare, int status, int pin, String brand, String model, String licencePlateNo,
                   String vehicleImage, String cusName, String cusPhone) {
        this.bookingId = bookingId;
        this.cusId = cusId;
        this.bookingTime = bookingTime;
        this.parkInTime = parkInTime;
        this.parkOutTime = parkOut;
        this.duration = duration;
        this.payCon = payCon;
        this.fare = fare;
        this.status = status;
        this.pin = pin;
        this.brand = brand;
        this.model = model;
        this.licencePlateNo = licencePlateNo;
        this.vehicleImage = vehicleImage;
        this.cusName = cusName;
        this.cusPhone = cusPhone;
    }

    public int getBookingId() {
        return bookingId;
    }


    public int getCusId() {
        return cusId;
    }


    public String getBookingTime() {
        return bookingTime;
    }


    public String getParkInTime() {
        return parkInTime;
    }


    public String getParkOutTime() {
        return parkOutTime;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isPayCon() {
        return payCon;
    }

    public String getFare() {
        return fare;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPin() {
        return pin;
    }


    public String getBrand() {
        return brand;
    }


    public String getModel() {
        return model;
    }


    public String getLicencePlateNo() {
        return licencePlateNo;
    }


    public String getVehicleImage() {
        return vehicleImage;
    }


    public String getCusName() {
        return cusName;
    }


    public String getCusPhone() {
        return cusPhone;
    }


}

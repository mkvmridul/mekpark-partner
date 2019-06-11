package com.example.mani.mekparkpartner.TowingPartner.Model;

import java.io.Serializable;

public class TowingBooking implements Serializable {

    private int bookingId;
    private int cusId;

    private String bookingTime; //Unix
    private String pickupTime; //Unix
    private String duration;
    private boolean payCon;

    private String baseFare;
    private String tax;
    private String addCharges;
    private String totalFare;


    private int status;
    private String pin;

    private String brand;
    private String model;
    private String licencePlateNo;
    private String vehicleImage;

    private String cusName;
    private String cusPhone;

    private String pickUpAddress;

    public TowingBooking(int bookingId, int cusId, String bookingTime, String pickupTime, String duration, boolean payCon,
                         String baseFare, String tax, String addCharges, String totalFare,
                         int status, String pin, String brand, String model, String licencePlateNo, String vehicleImage,
                         String cusName, String cusPhone,String pickUpAddress) {

        this.bookingId = bookingId;
        this.cusId = cusId;
        this.bookingTime = bookingTime;
        this.pickupTime = pickupTime;
        this.duration = duration;
        this.payCon = payCon;
        this.baseFare = baseFare;
        this.tax = tax;
        this.addCharges = addCharges;
        this.totalFare = totalFare;
        this.status = status;
        this.pin = pin;
        this.brand = brand;
        this.model = model;
        this.licencePlateNo = licencePlateNo;
        this.vehicleImage = vehicleImage;
        this.cusName = cusName;
        this.cusPhone = cusPhone;
        this.pickUpAddress = pickUpAddress;
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

    public String getPickupTime() {
        return pickupTime;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isPayCon() {
        return payCon;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public String getTax() {
        return tax;
    }

    public String getAddCharges() {
        return addCharges;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public int getStatus() {
        return status;
    }

    public String getPin() {
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

    public String getPickUpAddress() {
        return pickUpAddress;
    }
}

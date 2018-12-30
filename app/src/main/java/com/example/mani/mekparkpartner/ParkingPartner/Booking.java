package com.example.mani.mekparkpartner.ParkingPartner;

import java.io.Serializable;

public class Booking implements Serializable {

    private int bookingId;
    private int cusId;

    private String bookingTime;
    private String parkingTime;
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

    public Booking(int bookingId, int cusId, String bookingTime, String parkingTime, String duration, boolean payCon,
                   String fare, int status, int pin, String brand, String model, String licencePlateNo,
                   String vehicleImage, String cusName, String cusPhone) {
        this.bookingId = bookingId;
        this.cusId = cusId;
        this.bookingTime = bookingTime;
        this.parkingTime = parkingTime;
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

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCusId() {
        return cusId;
    }

    public void setCusId(int cusId) {
        this.cusId = cusId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(String parkingTime) {
        this.parkingTime = parkingTime;
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

    public void setPayCon(boolean payCon) {
        this.payCon = payCon;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
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

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicencePlateNo() {
        return licencePlateNo;
    }

    public void setLicencePlateNo(String licencePlateNo) {
        this.licencePlateNo = licencePlateNo;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getCusPhone() {
        return cusPhone;
    }

    public void setCusPhone(String cusPhone) {
        this.cusPhone = cusPhone;
    }
}

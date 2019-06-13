package com.example.mani.mekparkpartner.OffileParkingPartner.Model;

import java.io.Serializable;


public class OfflineParkingBooking implements Serializable {

    private int id;
    private String orderId;

    private String bookingTime;
    private String parkInTime;
    private String vehicleType;
    private String brand;
    private String model;
    private String licencePlateNo;
    private String custPhone;

    private String parkingType;
    private boolean payCompleted;

    private int status;              //0-ongoing, 1 - history
    private String remarks;
    private String farePerHr;


    //Only after booking is complete
    private String parkOutTime;
    private String duration;
    private String baseFare;
    private String tax;
    private String addCharges;
    private String totalFare;


    //For ongoing complete
    public OfflineParkingBooking(int id, String orderId, String bookingTime, String parkInTime, String vehicleType, String brand, String model, String licencePlateNo, String custPhone,
                                 String parkingType, boolean payCompleted, int status, String remarks,String farePerHr) {
        this.id = id;
        this.orderId = orderId;
        this.bookingTime = bookingTime;
        this.parkInTime = parkInTime;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.licencePlateNo = licencePlateNo;
        this.custPhone = custPhone;
        this.parkingType = parkingType;
        this.payCompleted = payCompleted;
        this.status = status;
        this.remarks = remarks;
        this.farePerHr = farePerHr;
    }


    //For offline complete
    public OfflineParkingBooking(int id, String orderId, String bookingTime, String parkInTime, String vehicleType, String brand, String model, String licencePlateNo, String custPhone, String parkingType, boolean payCompleted, int status, String remarks, String farePerHr,
                                 String parkOutTime, String duration, String baseFare, String tax, String addCharges, String totalFare) {
        this.id = id;
        this.orderId = orderId;
        this.bookingTime = bookingTime;
        this.parkInTime = parkInTime;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.licencePlateNo = licencePlateNo;
        this.custPhone = custPhone;
        this.parkingType = parkingType;
        this.payCompleted = payCompleted;
        this.status = status;
        this.remarks = remarks;
        this.farePerHr = farePerHr;
        this.parkOutTime = parkOutTime;
        this.duration = duration;
        this.baseFare = baseFare;
        this.tax = tax;
        this.addCharges = addCharges;
        this.totalFare = totalFare;
    }

    public int getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public String getParkInTime() {
        return parkInTime;
    }

    public String getVehicleType() {
        return vehicleType;
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

    public String getCustPhone() {
        return custPhone;
    }

    public String getParkingType() {
        return parkingType;
    }

    public boolean isPayCompleted() {
        return payCompleted;
    }

    public int getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getFarePerHr() {
        return farePerHr;
    }

    public String getParkOutTime() {
        return parkOutTime;
    }

    public String getDuration() {
        return duration;
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
}

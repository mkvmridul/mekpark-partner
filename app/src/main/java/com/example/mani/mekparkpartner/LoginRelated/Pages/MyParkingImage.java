package com.example.mani.mekparkpartner.LoginRelated.Pages;

public class MyParkingImage {

    int image_id;
    int imageName;

    public MyParkingImage(int image_id, int imageName) {
        this.image_id = image_id;
        this.imageName = imageName;
    }

    public int getImage_id() {
        return image_id;
    }

    public int getImageName() {
        return imageName;
    }
}

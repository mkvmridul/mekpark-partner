package com.example.mani.mekparkpartner.CommonForAllPartner;

public class SimpleImage {

    int image_id;
    String imageName;

    public SimpleImage(int image_id, String imageName) {
        this.image_id = image_id;
        this.imageName = imageName;
    }

    public int getImage_id() {
        return image_id;
    }

    public String getImageName() {
        return imageName;
    }
}

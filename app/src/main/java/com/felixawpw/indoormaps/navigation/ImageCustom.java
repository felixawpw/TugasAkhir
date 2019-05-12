package com.felixawpw.indoormaps.navigation;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;

import com.felixawpw.indoormaps.mirror.Map;

import java.io.File;

public class ImageCustom {
    Bitmap image;

    public ImageCustom() {

    }

    public ImageCustom(Bitmap image) {
        this.image = image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

//    public float getHeightCM() {
//        return image.getPhysicalHeightInch() * 2.54f;
//    }
//
//    public float getWidthCM() {
//        return image.getPhysicalWidthInch() * 2.54f;
//    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
    public Bitmap getImage() {
        return image;
    }

//    public ImageInfo getImageInfo() {
//        return imageInfo;
//    }
}
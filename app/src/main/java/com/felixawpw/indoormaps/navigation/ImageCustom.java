package com.felixawpw.indoormaps.navigation;

import android.graphics.ImageDecoder;

import com.felixawpw.indoormaps.mirror.Map;

import java.io.File;

public class ImageCustom {
    Map map;
    ImageDecoder.ImageInfo imageInfo;

    public ImageCustom() {

    }

    public ImageCustom(File imageFile) throws Exception {
        this.imageFile = imageFile;
    }

    public int getWidth() {
        return imageInfo.getWidth();
    }

    public int getHeight() {
        return imageInfo.getHeight();
    }

    public float getHeightCM() {
        return imageInfo.getPhysicalHeightInch() * 2.54f;
    }

    public float getWidthCM() {
        return imageInfo.getPhysicalWidthInch() * 2.54f;
    }

    public File getImageFile() {
        return imageFile;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }
}
package com.felixawpw.indoormaps.model;

import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.mirror.Tenant;

public class MarkerModel {

	private long mId;
	private String mImageURL;
	private String mText;
	private int mIconRes;
	private Marker marker;

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public Marker getMarker() {
		return marker;
	}

	public MarkerModel() {
	}

	public MarkerModel(long id, String imageURL, String text, int iconRes) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
	}

	public MarkerModel(long id, String imageURL, String text, int iconRes, Marker marker) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
		this.marker = marker;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getImageURL() {
		return mImageURL;
	}

	public void setImageURL(String imageURL) {
		mImageURL = imageURL;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public int getIconRes() {
		return mIconRes;
	}

	public void setIconRes(int iconRes) {
		mIconRes = iconRes;
	}

	@Override
	public String toString() {
		return mText;
	}
}

package com.felixawpw.indoormaps.model;

import com.felixawpw.indoormaps.mirror.Tenant;

public class TenantModel {
	
	private long mId;
	private String mImageURL;
	private String mText;
	private int mIconRes;
	private Tenant tenant;

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public TenantModel() {
	}

	public TenantModel(long id, String imageURL, String text, int iconRes) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
	}

	public TenantModel(long id, String imageURL, String text, int iconRes, Tenant tenant) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
		this.tenant = tenant;
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

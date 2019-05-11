package com.felixawpw.indoormaps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.felixawpw.indoormaps.mirror.Map;

public class MapsModel implements Parcelable {
	
	private long mId;
	private String mUrl;
	private String mTitle;
	private Map map;

	public MapsModel() {
	}

	protected MapsModel(Parcel in) {
		mId = in.readLong();
		mUrl = in.readString();
		mTitle = in.readString();
	}
	
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mUrl);
		dest.writeString(mTitle);
	}

	public static final Parcelable.Creator<MapsModel> CREATOR = new Parcelable.Creator<MapsModel>() {
		@Override
		public MapsModel createFromParcel(Parcel in) {
			return new MapsModel(in);
		}

		@Override
		public MapsModel[] newArray(int size) {
			return new MapsModel[size];
		}
	};

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
}
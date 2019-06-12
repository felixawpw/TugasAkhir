package com.felixawpw.indoormaps.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.felixawpw.indoormaps.AddNewPlacesActivity;
import com.felixawpw.indoormaps.AddedPlaceDetailsActivity;
import com.felixawpw.indoormaps.AddedPlacesActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.model.TenantModel;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.Collections;

public class ParallaxTravelAdapter extends BaseAdapter implements Swappable,
		OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<TenantModel> mTenantModelList;

	public ParallaxTravelAdapter(Context context,
                                 ArrayList<TenantModel> tenantModelList,
                                 boolean shouldShowDragAndDropIcon) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTenantModelList = tenantModelList;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return mTenantModelList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTenantModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mTenantModelList.get(position).getId();
	}

	@Override
	public void swapItems(int positionOne, int positionTwo) {
		Collections.swap(mTenantModelList, positionOne, positionTwo);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_parallax_travel, parent, false);
			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.list_item_parallax_travel_name);
			holder.text = (TextView) convertView
					.findViewById(R.id.list_item_parallax_travel_text);
			holder.show = (TextView) convertView.findViewById(R.id.list_item_parallax_travel_icon_show);
			holder.show.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		TenantModel dm = mTenantModelList.get(position);

		holder.name.setText(dm.getText());
		if (dm.getTenant() != null) {
			holder.text.setText(dm.getTenant().getGoogleMapsAddress());
		} else
			holder.text.setText("");
		
		holder.show.setTag(position);
		return convertView;
	}

	private static class ViewHolder {
		public TextView name;
		public TextView text;
		public TextView show;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag();
		TenantModel model = mTenantModelList.get(position);
		switch (v.getId()) {
			case R.id.list_item_parallax_travel_icon_show:
				Intent intent = null;
				if (model.getId() == 0) {
					intent = new Intent(mContext, AddNewPlacesActivity.class);
				}
				else {
					intent = new Intent(mContext, AddedPlaceDetailsActivity.class);
					intent.putExtra("tenant_id", model.getTenant().getId());
					intent.putExtra("tenant_name", model.getTenant().getNama());
					intent.putExtra("tenant_google_maps_id", model.getTenant().getGoogleMapsId());
				}
				mContext.startActivity(intent);
				break;
			default:
				break;
		}
	}
}

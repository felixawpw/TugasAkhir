package com.felixawpw.indoormaps.adapter;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Swappable;

public class DefaultAdapter extends BaseAdapter implements Swappable, UndoAdapter, OnDismissCallback {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<TenantModel> mTenantModelList;
	private boolean mShouldShowDragAndDropIcon;
	
	public DefaultAdapter(Context context, ArrayList<TenantModel> tenantModelList, boolean shouldShowDragAndDropIcon) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTenantModelList = tenantModelList;
		mShouldShowDragAndDropIcon = shouldShowDragAndDropIcon;
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
			convertView = mInflater.inflate(R.layout.list_item_default, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (TextView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		TenantModel dm = mTenantModelList.get(position);
		
		ImageUtil.displayRoundImage(holder.image, dm.getImageURL(), null);
		holder.text.setText(dm.getText());
		if (mShouldShowDragAndDropIcon) {
			holder.icon.setText(R.string.fontello_drag_and_drop);
		} else {
			holder.icon.setText(dm.getIconRes());
		}
		return convertView;
	}
	
	
	private static class ViewHolder {
		public ImageView image;
		public /*Roboto*/TextView text;
		public /*Fontello*/TextView icon;
	}

	@Override
	@NonNull
	public View getUndoClickView(@NonNull View view) {
		return view.findViewById(R.id.undo_button);
	}

	@Override
	@NonNull
	public View getUndoView(final int position, final View convertView,
			@NonNull final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.list_item_undo_view,
					parent, false);
		}
		return view;
	}

	@Override
	public void onDismiss(@NonNull final ViewGroup listView,
			@NonNull final int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			remove(position);
		}
	}
	public void remove(int position) {
		mTenantModelList.remove(position);
	}
}

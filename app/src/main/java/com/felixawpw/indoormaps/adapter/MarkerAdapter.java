package com.felixawpw.indoormaps.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.model.MarkerModel;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarkerAdapter extends ArrayAdapter<MarkerModel> implements Swappable, UndoAdapter, OnDismissCallback {

	private Context mContext;
	Activity activity;
	private LayoutInflater mInflater;
	private ArrayList<MarkerModel> mMarkerModelList;
	private boolean mShouldShowDragAndDropIcon;
	private Filter filter;

	public MarkerAdapter(Context context, ArrayList<MarkerModel> markerModelList, boolean shouldShowDragAndDropIcon, Activity activity) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMarkerModelList = markerModelList;
		mShouldShowDragAndDropIcon = shouldShowDragAndDropIcon;
		this.activity = activity;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return mMarkerModelList.size();
	}

	@Override
	public MarkerModel getItem(int position) {
		return mMarkerModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mMarkerModelList.get(position).getId();
	}

	@Override
	public void swapItems(int positionOne, int positionTwo) {
		Collections.swap(mMarkerModelList, positionOne, positionTwo);
	}

	public static final String TAG = MarkerAdapter.class.getSimpleName();
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_default, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (TextView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (activity instanceof MapActivity)
						((MapActivity)activity).showMarkerDetailDialog(((MapActivity)activity).getMarkerDataByIndex(position));
				}
			});
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MarkerModel dm = mMarkerModelList.get(position);
		
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

	public Filter getFilter() {
		if (filter == null)
			filter = new MarkerAdapter.AppFilter<MarkerModel>(mMarkerModelList);
		return filter;

	}

	private class AppFilter<T> extends Filter {

		private ArrayList<T> sourceObjects;

		public AppFilter(List<T> objects) {
			sourceObjects = new ArrayList<T>();
			synchronized (this) {
				sourceObjects.addAll(objects);
			}
		}

		@Override
		protected FilterResults performFiltering(CharSequence chars) {
			String filterSeq = chars.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (filterSeq != null && filterSeq.length() > 0) {
				ArrayList<T> filter = new ArrayList<T>();

				for (T object : sourceObjects) {
					// the filtering itself:
					if (object.toString().toLowerCase().contains(filterSeq))
						filter.add(object);
				}
				result.count = filter.size();
				result.values = filter;
			} else {
				// add all objects
				synchronized (this) {
					result.values = sourceObjects;
					result.count = sourceObjects.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
									  FilterResults results) {
			// NOTE: this function is *always* called from the UI thread.
			ArrayList<T> filtered = (ArrayList<T>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filtered.size(); i < l; i++)
				add((MarkerModel) filtered.get(i));
			notifyDataSetInvalidated();
		}
	}

}

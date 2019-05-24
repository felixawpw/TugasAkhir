package com.felixawpw.indoormaps.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.OwnerMapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.model.MarkerModel;
import com.felixawpw.indoormaps.navigation.Path;
import com.felixawpw.indoormaps.navigation.ProcessedStep;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.nhaarman.listviewanimations.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathAdapter extends ArrayAdapter<ProcessedStep> implements View.OnClickListener{

	private Context mContext;
	Activity activity;
	private LayoutInflater mInflater;
	private List<ProcessedStep> processedSteps;
	private boolean mShouldShowDragAndDropIcon;
	private Filter filter;

	public PathAdapter(Context context, List<ProcessedStep> processedSteps, boolean shouldShowDragAndDropIcon, Activity activity) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.processedSteps = processedSteps;
		mShouldShowDragAndDropIcon = shouldShowDragAndDropIcon;
		this.activity = activity;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return processedSteps.size();
	}

	@Override
	public ProcessedStep getItem(int position) {
		return processedSteps.get(position);
	}

	@Override
	public void swapItems(int positionOne, int positionTwo) {
		Collections.swap(processedSteps, positionOne, positionTwo);
	}

	public static final String TAG = PathAdapter.class.getSimpleName();
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final ProcessedStep dm = processedSteps.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_processed_step, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.textMapName = (TextView) convertView.findViewById(R.id.text_mapname);
			holder.layoutMain = (LinearLayout) convertView.findViewById(R.id.list_item_default_main_layout);
			holder.layoutMain.setTag(position);

			convertView.setTag(R.id.map_activity_holder, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.id.map_activity_holder);
		}

		holder.text.setText(dm.angleToString());
		holder.textMapName.setText("" + dm.getMapId());
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag(R.id.map_activity_marker_id);
        if (activity instanceof MapActivity)
            ((MapActivity)activity).showMarkerDetailDialog(position);
        else if (activity instanceof OwnerMapActivity)
			((OwnerMapActivity)activity).showMarkerDetailDialog(position);
	}

	private static class ViewHolder {
		public ImageView image;
		public /*Roboto*/TextView text, textMapName;
		public LinearLayout layoutMain;
		public Marker marker;
	}
}

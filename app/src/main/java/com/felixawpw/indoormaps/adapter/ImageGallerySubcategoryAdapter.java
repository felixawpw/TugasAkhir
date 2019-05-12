package com.felixawpw.indoormaps.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.felixawpw.indoormaps.AddNewMapActivity;
import com.felixawpw.indoormaps.AddedPlaceDetailsActivity;
import com.felixawpw.indoormaps.MapOwnerActivity;
import com.felixawpw.indoormaps.OwnerMapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.MapsModel;
import com.felixawpw.indoormaps.util.ImageUtil;

import java.util.ArrayList;

public class ImageGallerySubcategoryAdapter extends BaseAdapter implements View.OnClickListener {
	public static final String TAG = ImageGallerySubcategoryAdapter.class.getSimpleName();

    private static final int TYPE_ONE_COLUMN = 0;
    private static final int TYPE_TWO_COLUMNS = 1;
    private static final int TYPE_MAX_COUNT = TYPE_TWO_COLUMNS + 1;
    
	private LayoutInflater mInflater;
	private ArrayList<MapsModel> mImageGallerySubcategories;
	private Tenant tenant;
	private final boolean mIsLayoutOnTop;
	private Context context;
	private Activity activity;

	public ImageGallerySubcategoryAdapter(Context context,
                                          ArrayList<MapsModel> imageGallerySubcategories, boolean isLayoutOnTop, Tenant tenant, Activity activity) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImageGallerySubcategories = imageGallerySubcategories;
		mIsLayoutOnTop = isLayoutOnTop;
		this.context = context;
		this.tenant = tenant;
		this.activity = activity;
	}
	
    @Override
    public int getItemViewType(int position) {
    	if ((position == mImageGallerySubcategories.size() / 2)
    			&& (mImageGallerySubcategories.size() % 2 == 1)) {
    		return TYPE_ONE_COLUMN;
    	} else {
    		return TYPE_TWO_COLUMNS;
    	}
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

	@Override
	public int getCount() {
		return (mImageGallerySubcategories.size() / 2) + (mImageGallerySubcategories.size() % 2);
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		Log.i(TAG, "Selected Position = " + position);
		if (position == 0) {
			Intent intent = new Intent(activity, AddNewMapActivity.class);
			intent.putExtra("tenantId", tenant.getId());
			activity.startActivityForResult(intent, AddedPlaceDetailsActivity.REQUEST_ADD_NEW_MAP);
			return;
		}

		Map clickedMap = null;
		for(MapsModel model : mImageGallerySubcategories) {
			if (model.getMap() == null)
				continue;

			if (model.getMap().getId() == position)
				clickedMap = model.getMap();
		}

		Intent intent = new Intent(activity, OwnerMapActivity.class);
		intent.putExtra("mapId", clickedMap.getId());
		intent.putExtra("nama", clickedMap.getNama());
		intent.putExtra("deskripsi", clickedMap.getDeskripsi());
		intent.putExtra("height", clickedMap.getHeight());
		intent.putExtra("originalPath", clickedMap.getoriginalPath());
		intent.putExtra("processedPath", clickedMap.getprocessedPath());
		intent.putExtra("scaleLength", clickedMap.getScaleLength());
		intent.putExtra("scaleWidth", clickedMap.getScaleWidth());
		activity.startActivity(intent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder.OneColumnViewHolder oneColumnViewHolder = null;
		ViewHolder.TwoColumnsViewHolder twoColumnsViewHolder = null;
		int type = getItemViewType(position);
		if (type == TYPE_ONE_COLUMN) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_categories_one_column, parent, false);
				oneColumnViewHolder = new ViewHolder.OneColumnViewHolder();
				oneColumnViewHolder.image1 = (ImageView) convertView.findViewById(R.id.list_item_image_1);
				//oneColumnViewHolder.favoriteImage1 = (TextView) convertView.findViewById(R.id.list_item_favourite_1);
				oneColumnViewHolder.title1 = (TextView) convertView.findViewById(R.id.list_item_title_1);
				oneColumnViewHolder.numberOfImages1 = (TextView) convertView.findViewById(R.id.list_item_number_of_images_1);
				oneColumnViewHolder.layoutTopBottom1 = (ViewGroup) convertView.findViewById(R.id.layout_top_bottom_1);
				convertView.setTag(oneColumnViewHolder);
			} else {
				oneColumnViewHolder = (ViewHolder.OneColumnViewHolder) convertView.getTag();
			}
			MapsModel model1 = mImageGallerySubcategories.get(position * 2);
			
			if (TextUtils.isEmpty(model1.getTitle())) {
				oneColumnViewHolder.layoutTopBottom1.setVisibility(View.GONE);
			} else {
				oneColumnViewHolder.title1.setText(model1.getTitle());
			}
			ImageUtil.displayImage(oneColumnViewHolder.image1, model1.getUrl(), null);
			if (model1.getMap() != null)
				oneColumnViewHolder.image1.setTag(model1.getMap().getId());
			else
				oneColumnViewHolder.image1.setTag(0);

			//oneColumnViewHolder.favoriteImage1.setVisibility(View.GONE);
			oneColumnViewHolder.numberOfImages1.setVisibility(View.GONE);
			LayoutParams lp1 = (LayoutParams) oneColumnViewHolder.layoutTopBottom1.getLayoutParams();
			if (!mIsLayoutOnTop) {
				lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			} else {
				lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
			}
		} else if (type == TYPE_TWO_COLUMNS) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_categories_two_columns, parent, false);
				twoColumnsViewHolder = new ViewHolder.TwoColumnsViewHolder();
				twoColumnsViewHolder.image1 = (ImageView) convertView.findViewById(R.id.list_item_image_1);
				//twoColumnsViewHolder.favoriteImage1 = (TextView) convertView.findViewById(R.id.list_item_favourite_1);
				twoColumnsViewHolder.title1 = (TextView) convertView.findViewById(R.id.list_item_title_1);
				twoColumnsViewHolder.numberOfImages1 = (TextView) convertView.findViewById(R.id.list_item_number_of_images_1);
				twoColumnsViewHolder.layoutTopBottom1 = (ViewGroup) convertView.findViewById(R.id.layout_top_bottom_1);
				twoColumnsViewHolder.image2 = (ImageView) convertView.findViewById(R.id.list_item_image_2);
				//twoColumnsViewHolder.favoriteImage2 = (TextView) convertView.findViewById(R.id.list_item_favourite_2);
				twoColumnsViewHolder.title2 = (TextView) convertView.findViewById(R.id.list_item_title_2);
				twoColumnsViewHolder.numberOfImages2 = (TextView) convertView.findViewById(R.id.list_item_number_of_images_2);
				twoColumnsViewHolder.layoutTopBottom2 = (ViewGroup) convertView.findViewById(R.id.layout_top_bottom_2);
				convertView.setTag(twoColumnsViewHolder);
			} else {
				twoColumnsViewHolder = (ViewHolder.TwoColumnsViewHolder) convertView.getTag();
			}
			
			MapsModel model1 = mImageGallerySubcategories.get(position * 2);
			if (TextUtils.isEmpty(model1.getTitle())) {
				twoColumnsViewHolder.layoutTopBottom1.setVisibility(View.GONE);
			} else {
				twoColumnsViewHolder.title1.setText(model1.getTitle());
			}
			ImageUtil.displayImage(twoColumnsViewHolder.image1, model1.getUrl(), null);
			
			MapsModel model2 = mImageGallerySubcategories.get(position * 2 + 1);
			if (TextUtils.isEmpty(model2.getTitle())) {
				twoColumnsViewHolder.layoutTopBottom2.setVisibility(View.GONE);
			} else {
				twoColumnsViewHolder.title2.setText(model2.getTitle());
			}
			ImageUtil.displayImage(twoColumnsViewHolder.image2, model2.getUrl(), null);

			if (model1.getMap() != null)
				twoColumnsViewHolder.image1.setTag(model1.getMap().getId());
			else
				twoColumnsViewHolder.image1.setTag(0);

			if (model2.getMap() != null)
				twoColumnsViewHolder.image2.setTag(model2.getMap().getId());
			else
				twoColumnsViewHolder.image2.setTag(0);

			//twoColumnsViewHolder.favoriteImage1.setVisibility(View.GONE);
			twoColumnsViewHolder.numberOfImages1.setVisibility(View.GONE);
			//twoColumnsViewHolder.favoriteImage2.setVisibility(View.GONE);
			twoColumnsViewHolder.numberOfImages2.setVisibility(View.GONE);
			LayoutParams lp1 = (LayoutParams) twoColumnsViewHolder.layoutTopBottom1.getLayoutParams();
			LayoutParams lp2 = (LayoutParams) twoColumnsViewHolder.layoutTopBottom2.getLayoutParams();
			if (!mIsLayoutOnTop) {
				lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			} else {
				lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
				lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
			}
		}

		if (oneColumnViewHolder != null)
			oneColumnViewHolder.image1.setOnClickListener(this);

		if (twoColumnsViewHolder != null)
		{
			twoColumnsViewHolder.image1.setOnClickListener(this);
			twoColumnsViewHolder.image2.setOnClickListener(this);
		}

		return convertView;
	}
	
	/* We are not using favourite image here. If you really want to use it,
	 * remove commented lines related to favourite image. 
	 */
	private static class ViewHolder {
		public static class OneColumnViewHolder {
			public ImageView image1;
			//public TextView favoriteImage1;
			public TextView title1;
			public TextView numberOfImages1;
			public ViewGroup layoutTopBottom1;
		}
		
		private static class TwoColumnsViewHolder {
			public ImageView image1;
			//public TextView favoriteImage1;
			public TextView title1;
			public TextView numberOfImages1;
			public ViewGroup layoutTopBottom1;

			public ImageView image2;
			//public TextView favoriteImage2;
			public TextView title2;
			public TextView numberOfImages2;
			public ViewGroup layoutTopBottom2;
		}
	}
}

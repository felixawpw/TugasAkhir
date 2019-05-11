package com.felixawpw.indoormaps.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.felixawpw.indoormaps.AddNewPlacesActivity;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.util.ImageUtil;

public class GoogleCardsTravelAdapter extends ArrayAdapter<TenantModel>
        implements View.OnClickListener {

    private LayoutInflater mInflater;
    private List<TenantModel> items;
    private Filter filter;
    private Context context;
    public GoogleCardsTravelAdapter(Context context, List<TenantModel> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.list_item_google_cards_travel, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView
                    .findViewById(R.id.list_item_google_cards_travel_image);
            holder.categoryName = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_travel_category_name);
            holder.title = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_travel_title);
            holder.text = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_travel_text);
            holder.explore = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_travel_explore);
            holder.explore.setOnClickListener(this);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TenantModel item = getItem(position);
        ImageUtil.displayImage(holder.image, item.getImageURL(), null);
        holder.title.setText(item.getText());
        if (item.getText().equals("Add new place"))
            holder.explore.setText("ADD NEW");
        holder.explore.setTag(position);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView image;
        public TextView categoryName;
        public TextView title;
        public TextView text;
        public TextView explore;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.list_item_google_cards_travel_explore:
                TenantModel model = items.get(position);
                if (model.getText().equals("Add new place")) {
                    Intent intent = new Intent(context, AddNewPlacesActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("placeId", model.getTenant().getGoogleMapsId());
                    intent.putExtra("placeName", model.getTenant().getNama());
                    context.startActivity(intent);
                }
                // click on explore button
                break;
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<TenantModel>(items);
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
                add((TenantModel) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }
}

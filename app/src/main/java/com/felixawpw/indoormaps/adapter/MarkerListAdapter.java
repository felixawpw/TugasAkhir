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

import com.felixawpw.indoormaps.AddNewPlacesActivity;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.model.MarkerModel;
import com.felixawpw.indoormaps.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class MarkerListAdapter extends ArrayAdapter<MarkerModel>
        implements View.OnClickListener {

    private LayoutInflater mInflater;
    private List<MarkerModel> items;
    private Filter filter;
    private Context context;
    public MarkerListAdapter(Context context, List<MarkerModel> items) {
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

        MarkerModel item = getItem(position);
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

    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<MarkerModel>(items);
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

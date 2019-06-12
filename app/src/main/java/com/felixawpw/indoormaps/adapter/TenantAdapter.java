package com.felixawpw.indoormaps.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felixawpw.indoormaps.AddNewPlacesActivity;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.fragment.HomeFragment;
import com.felixawpw.indoormaps.fragment.PlacesFragment;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class TenantAdapter extends ArrayAdapter<TenantModel>
        implements View.OnClickListener {

    private LayoutInflater mInflater;
    private List<TenantModel> items;
    private Filter filter;
    private Context context;
    Fragment activity;

    public TenantAdapter(Context context, List<TenantModel> items, Fragment activity) {
        super(context, 0, items);
        this.context = context;
        this.activity = activity;
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

        final TenantModel dm = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_tenants, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.textAlamat = (TextView) convertView.findViewById(R.id.text_alamat);
            holder.icon = (TextView) convertView.findViewById(R.id.icon);
            holder.layoutMain = (LinearLayout) convertView.findViewById(R.id.list_item_default_main_layout);
            holder.layoutMain.setTag(position);
            holder.tenant = dm.getTenant();

            convertView.setTag(R.id.map_activity_holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.map_activity_holder);
        }

        convertView.setTag(R.id.map_activity_marker_id, position);
        convertView.setOnClickListener(this);
        ImageUtil.displayRoundImage(holder.image, dm.getImageURL(), null);
        holder.text.setText(dm.getText());
        if (dm.getTenant() != null) {
            holder.textAlamat.setText(dm.getTenant().getGoogleMapsAddress());
        }

        holder.icon.setText(R.string.fontello_play);
        return convertView;
    }

    private static class ViewHolder {
        public ImageView image;
        public /*Roboto*/TextView text;
        public TextView textAlamat;
        public /*Fontello*/TextView icon;
        public LinearLayout layoutMain;
        public Tenant tenant;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int position = (Integer) v.getTag(R.id.map_activity_marker_id);
        if (activity instanceof HomeFragment) {
            TenantModel model = items.get(position);
            try {
                ((HomeFragment)activity).placeData.writeHistory(model.getTenant(), HomeFragment.SEARCH_HISTORY_FILE_NAME);
            } catch (Exception ex) {
                Log.e("TenantAdapter", "Fail writing file " + ex.getMessage());
                ex.printStackTrace();
            }

            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("placeId", model.getTenant().getGoogleMapsId());
            intent.putExtra("placeName", model.getTenant().getNama());
            context.startActivity(intent);
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

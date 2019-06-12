package com.felixawpw.indoormaps.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.felixawpw.indoormaps.OwnerMapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Report;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.ReportModel;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.services.AuthServices;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportAdapter extends BaseExpandableListAdapter {
    public static final String TAG = ReportAdapter.class.getSimpleName();
    private Activity _context;

    public Activity getActivity() {
        return _context;
    }

    private List<ReportModel> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<ReportModel, String> _listDataChild;

    public ReportAdapter(Activity context, List<ReportModel> listDataHeader,
                                 HashMap<ReportModel, String> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_report_child, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.list_item_report_child_text_report);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)) == "" ? 0 : 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        final ReportModel dm = (ReportModel)getGroup(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_report_header, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.textAlamat = (TextView) convertView.findViewById(R.id.text_alamat);
            holder.icon = (TextView) convertView.findViewById(R.id.icon);
            holder.layoutMain = (LinearLayout) convertView.findViewById(R.id.list_item_default_main_layout);
            holder.layoutMain.setTag(position);
            holder.report = dm.getReport();

            holder.text.setText(dm.getTenantName() + " (" + dm.getReport().typeToString() + ")");
            holder.text.setTextColor(Color.BLACK);
            holder.textAlamat.setText(dm.getMarkerName());
            holder.icon.setText(R.string.fontello_play);

            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        getMapData(dm.getReport().getMarkerId());
                    } catch (Exception ex) {

                    }
                }
            });
        }
        return convertView;
    }

    public static final int GET_MAP_DATA_BY_MARKER_ID = 1;
    public static final String GET_MAP_DATA_BY_MARKER_ID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/process_report_marker/";

    public void handleResponse(int requestId, JSONObject response) {
        try {
            switch (requestId) {
                case GET_MAP_DATA_BY_MARKER_ID:
                    Log.i(TAG, "Response = " + response.toString());

                    boolean status = response.getBoolean("status");
                    String message = response.getString("message");
                    JSONObject mapData = response.getJSONObject("mapData");
                    int markerId = response.getInt("markerId");

                    Intent intent = new Intent(_context, OwnerMapActivity.class);
                    Map map = new Map(mapData);
                    intent.putExtra("mapId", map.getId());
                    intent.putExtra("nama", map.getNama());
                    intent.putExtra("deskripsi", map.getDeskripsi());
                    intent.putExtra("height", map.getHeight());
                    intent.putExtra("originalPath", map.getoriginalPath());
                    intent.putExtra("processedPath", map.getprocessedPath());
                    intent.putExtra("scaleLength", map.getScaleLength());
                    intent.putExtra("scaleWidth", map.getScaleWidth());
                    intent.putExtra("show_on_start_marker_id", markerId);

                    _context.startActivity(intent);
                    break;
                default:
                    break;
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error handling response : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getMapData(int markerId) throws JSONException {
        VolleyServices.getInstance(_context).httpRequest(
                Request.Method.GET,
                GET_MAP_DATA_BY_MARKER_ID_ADDRESS + markerId,
                _context,
                this,
                GET_MAP_DATA_BY_MARKER_ID,
                null);
    }



    private static class ViewHolder {
        public /*Roboto*/TextView text;
        public TextView textAlamat;
        public /*Fontello*/TextView icon;
        public LinearLayout layoutMain;
        public Report report;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

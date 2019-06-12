package com.felixawpw.indoormaps.mirror;

import org.json.JSONException;
import org.json.JSONObject;

public class Report {
    private String reportDetail;
    private String reportType;
    private int markerId;
    private int tenantId;

    public String typeToString() {
        switch (reportType) {
            case "0":
                return "Marker location is not accurate";
            case "1":
                return "Different marker name";
            default:
                return "";
        }
    }

    public Report() {

    }

    public Report(JSONObject json) throws JSONException{
        reportDetail = json.getString("report_detail");
        reportType = json.getString("report_type");
        markerId = json.getInt("marker_id");
        tenantId = json.getInt("tenant_id");
    }

    public String getReportDetail() {
        return reportDetail;
    }

    public void setReportDetail(String reportDetail) {
        this.reportDetail = reportDetail;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getMarkerId() {
        return markerId;
    }

    public void setMarkerId(int markerId) {
        this.markerId = markerId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
}

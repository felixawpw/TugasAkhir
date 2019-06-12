package com.felixawpw.indoormaps.model;

import com.felixawpw.indoormaps.mirror.Report;

public class ReportModel {
    private long mId;
    private String tenantName;
    private String markerName;
    private Report report;

    public ReportModel() {

    }

    public ReportModel(Report report, String tenantName, String markerName) {
        this.report = report;
        this.tenantName = tenantName;
        this.markerName = markerName;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}

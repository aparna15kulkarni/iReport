package com.ireport.controller.utils.httpUtils.APIHandlers;

import android.content.Context;
import android.util.Log;

import com.ireport.activities.ICallbackActivity;
import com.ireport.controller.IReportException;
import com.ireport.controller.utils.Constants;
import com.ireport.model.LocationDetails;
import com.ireport.model.ReportData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Somya on 12/10/2016.
 */

public class FilterReportsAPIHandler extends HttpBaseCommunicator {
    private ICallbackActivity activity_;
    private String callbackIdentifier_;
    private String emailId;
    private ArrayList<String> statusList;

    public FilterReportsAPIHandler(
            ICallbackActivity activity,
            String identifier,
            String emailId,
            ArrayList<String>statusList
    ) {
        this.activity_ = activity;
        this.callbackIdentifier_ = identifier;
        this.emailId = emailId;
        this.statusList = statusList;
    }

    @Override
    protected String getRequestURL() throws IReportException {
        return Constants.SERVER_URL + ":" + Constants.SERVER_PORT + "/filterReports";
    }

    @Override
    protected void handleResponse(String response) throws IReportException {
        ArrayList<ReportData> reportList = parseResponseForReports(response);

        // create the final object here. test code for now
        this.activity_.onPostProcessCompletion(
                reportList,
                this.callbackIdentifier_,
                true    /* is error */);
    }

    @Override
    protected Map<String, String> getPostParams() {
        HashMap<String,String> params = new HashMap<>();
        if(emailId != null) {
            params.put("query_email", emailId);
        }

        if(statusList != null && statusList.size() > 0) {
            String statusStr = statusList.get(0);
            for(int id = 1; id < statusList.size(); id++) {
                statusStr += ","+statusList.get(id);
            }
            params.put("query_status",statusStr);
        }
        Log.d("PARAMS",params.toString());
        return params;
    }

    public void filterReports(Context ctx) {
        this.sendHttpPostRequest(ctx);
        System.out.println("returning from UI thread immediately");
    }

    private ArrayList<ReportData> parseResponseForReports (String response) {
        ArrayList<ReportData> riData = new ArrayList<>();

        try {

            JSONObject json = new JSONObject(response);

            JSONArray arrJson= json.getJSONArray("data");
            String[] arr = new String[arrJson.length()];

            for(int i = 0; i < arrJson.length(); i++) {
                arr[i] = arrJson.getString(i);

                JSONObject tempJson = new JSONObject(arr[i]);
                ReportData rd = new ReportData();

                String email = tempJson.getString("user_email");
                rd.setReporteeID(email);

                String description = tempJson.getString("description");
                rd.setDescription(description);

                String size = tempJson.getString("size");
                rd.setSize(size);

                String severity = tempJson.getString("severity_level");
                rd.setSeverityLevel(severity);

                String pic = tempJson.getString("pictures");
                rd.setImages(pic);

                String status = tempJson.getString("status");
                rd.setStatus(status);

                String reportId = tempJson.getString("_id");
                rd.setReportId(reportId);

                if (tempJson.has("street_address")) {
                    String streetAdd = tempJson.getString("street_address");
                    rd.setStreetAddress(streetAdd);
                }

                String timeStamp = tempJson.getString("timestamp");
                rd.setTimestamp(timeStamp);

                if(tempJson.has("location")) {
                    String location = tempJson.getString("location");
                    JSONObject locJson = new JSONObject(location);

                    String lat = locJson.getString("lat");
                    String lng = locJson.getString("lng");

                    rd.setLocation(new LocationDetails(Double.valueOf(lat), Double.valueOf(lng)));
                }
                riData.add(rd);
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }
        return riData;
    }
}

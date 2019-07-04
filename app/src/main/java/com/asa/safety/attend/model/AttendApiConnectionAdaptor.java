package com.asa.safety.attend.model;

import android.content.res.Resources;
import android.util.Log;

import com.asa.safety.attend.object.Attendance;
import com.asa.safety.attend.objectManager.AttendObjectManager;
import com.asa.safety.R;
import com.asa.safety.utils.model.ApiConnectionAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AttendApiConnectionAdaptor extends ApiConnectionAdaptor {
    private final static String tag = "AttendApiAdaptor";

    private String base_url;
    private String postAttendanceUrl;

    public AttendApiConnectionAdaptor(Resources res) {
        super(res);
        initVar();
    }

    private void initVar() {
        base_url = res.getString(R.string.api_safety_base_url);
        postAttendanceUrl = base_url + res.getString(R.string.post_attendance_url);
    }

    public boolean postAttendance(String postData) {
        String resultJson = tryExecuteAndGetFromServer(postAttendanceUrl, postData);
        return isSuccess(resultJson);
    }

    public void postAttendancesToServer(String localMacAddress) {
        if (AttendObjectManager.filteredAttendanceList.isEmpty()) {
            return;
        }
        List<Attendance> postingAttendanceList = new ArrayList<>(AttendObjectManager.filteredAttendanceList);
        String postData = getJsonPostData(postingAttendanceList);
        if (postAttendance(postData)) {
            for (Attendance attendance:postingAttendanceList) {
                AttendObjectManager.filteredAttendanceList.remove(attendance);
            }
            Log.e(tag, "Post Successful, Sent size: "+postingAttendanceList.size()+", Remain list size: "+AttendObjectManager.filteredAttendanceList.size());
        } else {
            Log.e(tag, "fail to post");
        }
    }

    private static String getJsonPostData(List<Attendance> attendanceList) {
        JSONObject postData = new JSONObject();
        try {
            JSONArray attendanceListJson = new JSONArray(attendanceList.toString());
            postData.put("attendanceList", attendanceListJson);
        } catch (JSONException e) {
            Log.e(tag, "getJsonPostData: "+e.toString());
        }
        return postData.toString();
    }
}

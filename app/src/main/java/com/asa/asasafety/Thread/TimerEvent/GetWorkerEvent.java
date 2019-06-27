package com.asa.asasafety.Thread.TimerEvent;

import android.app.Activity;
import android.widget.TextView;

import com.asa.asasafety.Model.ApiConnectionAdaptor;
import com.asa.asasafety.Object.Worker;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.asa.asasafety.R;
import com.asa.asasafety.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetWorkerEvent extends TimerEvent {
    private ApiConnectionAdaptor apiConnectionAdaptor;
    private JSONObject postData;
    private Activity activity;

    public GetWorkerEvent(Activity activity) throws JSONException {
        super(1 * 60 * 24 * 30, true);
        this.activity = activity;
        this.apiConnectionAdaptor = new ApiConnectionAdaptor(activity.getResources());
        postData = new JSONObject();
        postData.put("deviceId", Utils.getSharePreference(activity).getString("mac", ""));
    }

    protected void event() {
        setWorkersFromServer();
    }

    private void setWorkersFromServer() {
        List<Worker> workerList = apiConnectionAdaptor.getCurrentWorkerListAndSetLastUpdated(postData.toString());
        SafetyObjectManager.setWorkerList(workerList);
        SafetyObjectManager.workerLastUpdated = apiConnectionAdaptor.getWorkerLastUpdated();
        updateUI2CurrentTime();
    }

    private void updateUI2CurrentTime() {
        ((TextView)activity.findViewById(R.id.tv_update_dz)).setText(SafetyObjectManager.workerLastUpdated);
    }
}

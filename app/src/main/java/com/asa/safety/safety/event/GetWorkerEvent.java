package com.asa.safety.safety.event;

import android.app.Activity;
import android.widget.TextView;

import com.asa.safety.safety.model.SafetyApiConnectionAdaptor;
import com.asa.safety.safety.object.Worker;
import com.asa.safety.safety.objectManager.SafetyObjectManager;
import com.asa.safety.R;
import com.asa.safety.utils.Utils;
import com.asa.safety.utils.thread.event.TimerEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetWorkerEvent extends TimerEvent {
    protected String eventName = "GetWorkerEvent";

    private SafetyApiConnectionAdaptor safetyApiConnectionAdaptor;
    private JSONObject postData;
    private Activity activity;

    public GetWorkerEvent(Activity activity) throws JSONException {
        super(1 * 60 * 24 * 30, true);
        this.activity = activity;
        this.safetyApiConnectionAdaptor = new SafetyApiConnectionAdaptor(activity.getResources());
        postData = new JSONObject();
        postData.put("deviceId", Utils.getMacFromSharedPreference(activity));
    }

    protected void event() {
        setWorkersFromServer();
    }

    private void setWorkersFromServer() {
        List<Worker> workerList = safetyApiConnectionAdaptor.getCurrentWorkerListAndSetLastUpdated(postData.toString());
        SafetyObjectManager.setWorkerList(workerList);
        SafetyObjectManager.workerLastUpdated = safetyApiConnectionAdaptor.getWorkerLastUpdated();
        updateUI2CurrentTime();
    }

    private void updateUI2CurrentTime() {
        ((TextView)activity.findViewById(R.id.tv_update_dz)).setText(SafetyObjectManager.workerLastUpdated);
    }
}

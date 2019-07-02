package com.asa.attend.thread.event;

import android.util.Log;

import com.asa.attend.model.AttendApiConnectionAdaptor;
import com.asa.attend.objectManager.AttendObjectManager;
import com.asa.utils.thread.event.TimerEvent;

public class TakeAttendanceEvent extends TimerEvent {
    protected String eventName = "TakeAttendanceEvent";
    private final String tag = "TakeAttendanceEvent";

    private AttendApiConnectionAdaptor attendApiConnectionAdaptor;
    private String localMacAddress;

    public TakeAttendanceEvent(AttendApiConnectionAdaptor attendApiConnectionAdaptor, String localMacAddress) {
        super(5, false);
        this.attendApiConnectionAdaptor = attendApiConnectionAdaptor;
        this.localMacAddress = localMacAddress;
    }

    public void event() {
        if (AttendObjectManager.filteredAttendanceList!=null)
            attendApiConnectionAdaptor.postAttendancesToServer(localMacAddress);
        else
            Log.e(tag, "null list");
    }
}

package com.asa.asasafety.ObjectManager;

import com.asa.asasafety.Object.DangerZone;
import com.asa.asasafety.Object.Worker;

import java.util.List;

public class SafetyObjectManager {
    public static List<Worker> workerList;
    public static List<DangerZone> dangerZoneList;

    public static void setWorkerList(List<Worker> workerList) {
        SafetyObjectManager.workerList = workerList;
    }

    public static List<Worker> getWorkerList() {
        return workerList;
    }

    public static List<DangerZone> getDangerZoneList() {
        return dangerZoneList;
    }

    public static void setDangerZoneList(List<DangerZone> dangerZoneList) {
        SafetyObjectManager.dangerZoneList = dangerZoneList;
    }
}

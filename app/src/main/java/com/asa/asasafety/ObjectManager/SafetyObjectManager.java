package com.asa.asasafety.ObjectManager;

import com.asa.asasafety.Object.DangerZone;
import com.asa.asasafety.Object.DangerZoneCondition;
import com.asa.asasafety.Object.Smartag;
import com.asa.asasafety.Object.SmartagFactory;
import com.asa.asasafety.Object.VirtualSmartag;
import com.asa.asasafety.Object.Worker;
import com.moko.support.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class SafetyObjectManager {
    private final static String tag = "SafetyObjectManager";

    public static List<Worker> workerList;
    public static List<DangerZone> dangerZoneList;
    public static List<Smartag> smartagsInDangerZone;
    public static List<Smartag> filteredSmartags;
    public static List<VirtualSmartag> filteredVirtualSmartags;


    public static void minorRemainLightingTimeByOne() {
        for (int i = 0; i< filteredSmartags.size(); i++) {
            filteredSmartags.get(i).minorRemainLightingEndTime();
        }
    }

    public static void minorRemainNextAlertTimeByOne() {
        for (int i = 0; i< filteredVirtualSmartags.size(); i++) {
            filteredVirtualSmartags.get(i).minorRemainNextAlertTime();
        }
    }

    public static void checkAndInitList() {
        if (workerList==null) { workerList = new ArrayList<>(); }
        if (dangerZoneList==null) { dangerZoneList = new ArrayList<>(); }
        if (filteredSmartags ==null) { filteredSmartags = new ArrayList<>(); }
        if (smartagsInDangerZone==null) { smartagsInDangerZone = new ArrayList<>(); }
        if (filteredVirtualSmartags ==null) { filteredVirtualSmartags = new ArrayList<>(); }
    }

    public static void removeOldSmartagRecords() {
        int listSize = filteredSmartags.size();
        for (int i=0; i<listSize; i++) {
            boolean isNoConnectionTimes = filteredSmartags.get(i).getRemainConnectionTimes()<=0;
            boolean isNoLightingEndTime = filteredSmartags.get(i).getRemainLightingEndTime()<=0;
            if (isNoConnectionTimes||isNoLightingEndTime) {
                filteredSmartags.remove(i);
                listSize--;
                i--;
            }
        }
    }

    public static void removeOldVirtualSmartagRecords() {
        int listSize = filteredVirtualSmartags.size();
        for (int i=0; i<listSize; i++) {
            boolean isReachNextAlertTime = filteredVirtualSmartags.get(i).getRemainNextAlertTime()<=0;
            if (isReachNextAlertTime) {
                filteredVirtualSmartags.remove(i);
                listSize--;
                i--;
            }
        }
    }

    public static void checkAndAppendVirtualSmartagList(DeviceInfo deviceInfo) {
        VirtualSmartag virtualSmartag = SmartagFactory.getVirtualSmartag(deviceInfo);
        if (!filteredVirtualSmartags.contains(virtualSmartag)) {
            filteredVirtualSmartags.add(virtualSmartag);
        }
    }

    public static void addSmartagList(Smartag smartag) {
        if (!filteredSmartags.contains(smartag)) {
            filteredSmartags.add(smartag);
        }
    }

    public static boolean isFitDangerZone(DeviceInfo deviceInfo) {
        boolean isFitDangerZone = false;
        boolean isTargetSmartag;
        boolean isFitRssiConditions;
        boolean isInScanTime;

        for (DangerZone dangerZone:dangerZoneList) {
            isTargetSmartag = isInDisallowTradeCodes(dangerZone, deviceInfo) || isInDisallowWorkerCardIds(dangerZone, deviceInfo);
            isFitRssiConditions = isFitRssiConditions(dangerZone, deviceInfo);
            isInScanTime = isScanTime(dangerZone);
            isFitDangerZone = isTargetSmartag && isFitRssiConditions && isInScanTime;
            if (isFitDangerZone) {
                break;
            }
        }
        return isFitDangerZone;
    }

    private static boolean isInDisallowTradeCodes(DangerZone dangerZone, DeviceInfo deviceInfo) {
        boolean isInDisallowTradeCodes = false;
        for (String tradeCode:dangerZone.getDisallowTradeCodes()) {
            if (tradeCode.equals(getTradeCode(deviceInfo))) {
                isInDisallowTradeCodes = true;
                break;
            }
        }
        return isInDisallowTradeCodes;
    }

    private static String getTradeCode(DeviceInfo deviceInfo) {
        String tradeCode = "N/A";
        for (Worker worker:workerList) {
            if (worker.getHelmetId().equals(deviceInfo.mac)) {
                tradeCode = worker.getTradeCode();
                break;
            }
        }
        return tradeCode;
    }

    private static boolean isInDisallowWorkerCardIds(DangerZone dangerZone, DeviceInfo deviceInfo) {
        boolean isInDisallowWorkerCardIds = false;
        for (String workerCardId:dangerZone.getDisallowWorkerCardIds()) {
            if (workerCardId.equals(getWorkerCardId(deviceInfo))) {
                isInDisallowWorkerCardIds = true;
                break;
            }
        }
        return isInDisallowWorkerCardIds;
    }

    private static String getWorkerCardId(DeviceInfo deviceInfo) {
        String workerCardId = "N/A";
        for (Worker worker:workerList) {
            if (worker.getHelmetId().equals(deviceInfo.mac)) {
                workerCardId = worker.getCardId();
                break;
            }
        }
        return workerCardId;
    }

    private static boolean isFitRssiConditions(DangerZone dangerZone, DeviceInfo deviceInfo) {
        boolean isFitRssiConditions = false;
        for (DangerZoneCondition condition:getMatchedConditions(dangerZone.getConditions(), deviceInfo)) {
            switch (condition.getOp()) {
                case "gte": isFitRssiConditions = deviceInfo.rssi >= condition.getRssi(); break;
                case "lte": isFitRssiConditions = deviceInfo.rssi <= condition.getRssi(); break;
            }
            if (isFitRssiConditions) break;
        }
        return isFitRssiConditions;
    }

    private static List<DangerZoneCondition> getMatchedConditions(List<DangerZoneCondition> conditions, DeviceInfo deviceInfo) {
        List<DangerZoneCondition> matchedConditions = new ArrayList<>();
        for (DangerZoneCondition condition:conditions) {
            if (condition.getHubid().equals(deviceInfo.mac)) {
                matchedConditions.add(condition);
            }
        }
        return matchedConditions;
    }

    private static boolean isScanTime(DangerZone dangerZone) {
        boolean isScanTime = false;
        //TODO
        isScanTime = true;
        return isScanTime;
    }

    private static void addSmartagsIfNotExist(List<Smartag> smartagList, Smartag smartag) {
        if (!smartagList.contains(smartag)) {
            smartagList.add(smartag);
        }
    }

    public static void setWorkerList(List<Worker> workerList) {
        SafetyObjectManager.workerList = workerList;
    }

    public static List<DangerZone> getDangerZoneList() {
        return dangerZoneList;
    }

    public static void setDangerZoneList(List<DangerZone> dangerZoneList) {
        SafetyObjectManager.dangerZoneList = dangerZoneList;
    }
}

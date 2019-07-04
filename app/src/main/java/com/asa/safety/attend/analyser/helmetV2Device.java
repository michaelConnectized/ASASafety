package com.asa.safety.attend.analyser;

import java.util.ArrayList;
import java.util.Arrays;

public class helmetV2Device {
    //0x01
    private ArrayList<String> flags = new ArrayList<String>();
    //0x16
    private ArrayList<String> servicesData = new ArrayList<String>();

    private boolean isPackage2 = false;

    private String eddyStoneUUID = "";
    private String shortedName = "";

    private int txPower = -999;
    private int batteryLevel;


    public helmetV2Device(byte[] oriData) {
        int pointer = 0;
        int length = 0;
        int type = 0;

        String tmpString = "";
        byte[] tmpData;


        while (pointer<oriData.length-1) {
            tmpString = "";
            length = Arrays.copyOfRange(oriData, pointer, pointer+1)[0];
            type = Arrays.copyOfRange(oriData, pointer+1, pointer+2)[0];
            //end
            if (pointer+1+length < pointer+2) {
                break;
            }
            tmpData = Arrays.copyOfRange(oriData, pointer+2, pointer+1+length);
            for (int i=0; i<tmpData.length; i++) {
                tmpString += String.format("%1$02X",tmpData[i]);
            }
            pointer += length + 1;

            switch (type) {
                case 1: flags.add(tmpString); break;
                case 3: eddyStoneUUID = tmpString; break;
                case 22: servicesData.add(tmpString);
                    shortedName = new String(Arrays.copyOfRange(tmpData, 5, 14));

                    String tmpUUID = "";
                    byte[] tmpByte = Arrays.copyOfRange(tmpData, 0, 2);
                    for (int i=0; i<2; i++) {
                        tmpUUID += String.format("%1$02X",tmpByte[i]);
                    }

                    if (tmpUUID.equals("AAFE")) { //10FF is the package stored txPower
                        isPackage2 = false;
                        txPower = Arrays.copyOfRange(tmpData, 3, 4)[0];
                    } else if (tmpUUID.equals("10FF")) { //10FF is the package stored Battery Power
                        isPackage2 = true;
                        batteryLevel = Arrays.copyOfRange(tmpData, 12, 13)[0];
                    }
                    break;
            }
        }

    }

    public boolean isHelmetV2Device() {
        if (eddyStoneUUID.contains("AAFE")) {
            if (shortedName.equals("infosmart"))
                return true;
        }
        return false;
    }

    public boolean isPackage2() {
        return isPackage2;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }

    public ArrayList<String> getServicesData() {
        return servicesData;
    }

    public String byteToString(byte[] bs) {
        String totoal = "";
        for (byte b : bs) {
            totoal += String.format("%1$02X",b);
        }
        return totoal;
    }

    public int getTxPower() {
        return txPower;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }
}

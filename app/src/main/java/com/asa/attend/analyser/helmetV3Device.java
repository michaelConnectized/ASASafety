package com.asa.attend.analyser;

import java.util.ArrayList;
import java.util.Arrays;

public class helmetV3Device {
    //0x01
    private ArrayList<String> flags = new ArrayList<String>();
    //0x08
    private ArrayList<String> shortenedLocalNames = new ArrayList<String>();
    //0x0A
    private ArrayList<Integer> txPowers = new ArrayList<Integer>();
    //0x16
    private ArrayList<String> servicesData = new ArrayList<String>();

    private String hardwareVersion = "";
    private String firmwareVersion = "";
    private String macAddress = "";


    public helmetV3Device(byte[] oriData) {
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
                case 8: shortenedLocalNames.add(new String(tmpData)); break;
                case 10: txPowers.add(tmpData[0] & 0xFF); break;
                case 22: servicesData.add(tmpString);
                        if (tmpString.substring(0,4).equals("10FE")) {
                            hardwareVersion = tmpString.substring(4, 8);
                            firmwareVersion = tmpString.substring(8, 13);
                            macAddress = tmpString.substring(13, 26);
                        }
                        break;
            }
        }
        /*
        LogManager.d("testHel", flags.toString());
        LogManager.d("testHel", shortenedLocalNames.toString());
        LogManager.d("testHel", txPowers.toString());
        LogManager.d("testHel", servicesData.toString());
        LogManager.d("testHel", hardwareVersion);
        LogManager.d("testHel", firmwareVersion);
        LogManager.d("testHel", macAddress);*/
    }

    public boolean isHelmetV3Device() {
        if (shortenedLocalNames.contains("ITT"))
            return true;
        else
            return false;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }

    public ArrayList<String> getShortenedLocalNames() {
        return shortenedLocalNames;
    }

    public ArrayList<Integer> getTxPowers() {
        return txPowers;
    }

    public ArrayList<String> getServicesData() {
        return servicesData;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getMacAddress() {
        return macAddress;
    }
}

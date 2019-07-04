package com.asa.safety.attend.analyser;

import java.util.ArrayList;
import java.util.Arrays;

public class helmetV1Device {
    //0x01
    private ArrayList<String> flags = new ArrayList<String>();
    //0x16
    private ArrayList<String> servicesData = new ArrayList<String>();

    private String eddyStoneUUID = "";
    private String shortedName = "";


    public helmetV1Device(byte[] oriData) {
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
                        shortedName = new String(Arrays.copyOfRange(tmpData, 4, 7));
                        break;
            }
        }

        //LogManager.d("beacone", servicesData.toString());

    }

    public boolean isHelmetV1Device() {
        if (eddyStoneUUID.contains("AAFE")) {
            if (shortedName.equals("ITT"))
                return true;
        }
        return false;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }


    public ArrayList<String> getServicesData() {
        return servicesData;
    }

}

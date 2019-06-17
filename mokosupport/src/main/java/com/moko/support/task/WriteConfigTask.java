package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

/**
 * @Date 2018/1/20
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.WriteConfigTask
 */
public class WriteConfigTask extends OrderTask {
    public byte[] data;

    public WriteConfigTask(MokoOrderTaskCallback callback, int responseType) {
        super(OrderType.writeConfig, callback, responseType);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ConfigKeyEnum key) {
        switch (key) {
            case GET_SLOT_TYPE:
            case GET_DEVICE_MAC:
            case GET_DEVICE_NAME:
            case GET_CONNECTABLE:
            case GET_IBEACON_UUID:
            case GET_IBEACON_INFO:
            case GET_FAST_MODE:
            case GET_SLOW_MODE:
            case SET_CLOSE:
                createGetConfigData(key.getConfigKey());
                break;
        }
    }

    private void createGetConfigData(int configKey) {
        data = new byte[]{(byte) 0xEA, (byte) configKey, (byte) 0x00, (byte) 0x00};
    }

    public void setiBeaconData(int major, int minor, int advTxPower) {
        String value = "EA" + MokoUtils.int2HexString(ConfigKeyEnum.SET_IBEACON_INFO.getConfigKey()) + "0005"
                + String.format("%04X", major) + String.format("%04X", minor) + MokoUtils.int2HexString(Math.abs(advTxPower));
        data = MokoUtils.hex2bytes(value);
    }

    public void setiBeaconUUID(String uuidHex) {
        String value = "EA" + MokoUtils.int2HexString(ConfigKeyEnum.SET_IBEACON_UUID.getConfigKey()) + "0010"
                + uuidHex;
        data = MokoUtils.hex2bytes(value);
    }

    public void setDeviceName(String deviceName) {
        String deviceNameHex = MokoUtils.string2Hex(deviceName);
        String value = "EA" + MokoUtils.int2HexString(ConfigKeyEnum.SET_DEVICE_NAME.getConfigKey()) + "00"
                + MokoUtils.int2HexString(deviceNameHex.length() / 2) + deviceNameHex;
        data = MokoUtils.hex2bytes(value);
    }

    public void setConneactable(boolean isConnectable) {
        String value = "EA" + MokoUtils.int2HexString(ConfigKeyEnum.SET_CONNECTABLE.getConfigKey()) + "0001"
                + (isConnectable ? "01" : "00");
        data = MokoUtils.hex2bytes(value);
    }

    public void setLEDInfo(boolean controlEnable, boolean always, int timeout, int LedOn, int LedOff) {
        String value = "EA" + MokoUtils.int2HexString(ConfigKeyEnum.SET_LED_INFO.getConfigKey()) + "0008"
                + (controlEnable ? "01" : "00") + (always ? "01" : "00") + String.format("%04X", timeout) + String.format("%04X", LedOn) + String.format("%04X", LedOff);
        data = MokoUtils.hex2bytes(value);
    }

    public void setFastMode(boolean FastEnable, int timeout, int advInterval, int advTxPower, int txPower) {
        String value = "EA" + MokoUtils.int2HexString(ConfigKeyEnum.SET_FAST_MODE.getConfigKey()) + "0006"
                + (FastEnable ? "01" : "00") + String.format("%02X", timeout) + String.format("%04X", advInterval) + MokoUtils.bytesToHexString(MokoUtils.toByteArray(advTxPower, 1)) + MokoUtils.bytesToHexString(MokoUtils.toByteArray(txPower, 1));
        data = MokoUtils.hex2bytes(value);
    }

    public void setSlowMode(int advInterval, int advTxPower, int txPower) {
        String value = "EA" + MokoUtils.int2HexString(ConfigKeyEnum.SET_SLOW_MODE.getConfigKey()) + "0004"
                + String.format("%04X", advInterval) + MokoUtils.bytesToHexString(MokoUtils.toByteArray(advTxPower, 1)) + MokoUtils.bytesToHexString(MokoUtils.toByteArray(txPower, 1));
        data = MokoUtils.hex2bytes(value);
    }
}

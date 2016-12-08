package com.blublabs.magicmirror.settings.mirror.wifi;

import android.support.annotation.NonNull;

/**
 * Created by andrs on 05.12.2016.
 */

public class WifiNetwork {

    private String ssid;
    private String macAddress;
    private boolean connected;

    public WifiNetwork(@NonNull String ssid, @NonNull String macAddress) {
        this.ssid = ssid;
        this.macAddress = macAddress;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WifiNetwork that = (WifiNetwork) o;

        if (!getSsid().equals(that.getSsid())) return false;
        return getMacAddress().equals(that.getMacAddress());

    }

    @Override
    public int hashCode() {
        int result = getSsid().hashCode();
        result = 31 * result + getMacAddress().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getSsid() + " (" + getMacAddress() + ")";
    }
}

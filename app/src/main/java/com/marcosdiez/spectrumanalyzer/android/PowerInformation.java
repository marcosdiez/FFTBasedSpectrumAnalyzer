package com.marcosdiez.spectrumanalyzer.android;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 04-May-15.
 */
public class PowerInformation {

    public int getBatteryPercent() {
        return batteryPercent;
    }

    public boolean isCharging() {
        return charging;
    }

    int batteryPercent = 0;
    boolean charging = false;


    public PowerInformation() {
        Intent batteryStatus = getBatteryInformationIntent();

        parseBatteryPercent(batteryStatus);

        parseIsCharging(batteryStatus);
    }

    private Intent getBatteryInformationIntent() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        return Globals.getContext().registerReceiver(null, ifilter);
    }

    private void parseIsCharging(Intent batteryStatus) {
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        charging = chargePlug == BatteryManager.BATTERY_PLUGGED_USB ||
                chargePlug == BatteryManager.BATTERY_PLUGGED_AC ||
                chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }

    private void parseBatteryPercent(Intent batteryStatus) {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        batteryPercent = (int) (level * 100 / (float) scale);
    }
}

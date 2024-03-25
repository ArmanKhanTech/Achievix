package com.android.achievix.Utility;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

public class NetworkUtil {
    public static float getUID(long startMillis, long endMillis, String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo info = null;

        try {
            info = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        }

        int uid = Objects.requireNonNull(info).uid;
        return fetchNetworkStatsInfo(startMillis, endMillis, uid, context);
    }

    public static float fetchNetworkStatsInfo(long startMillis, long endMillis, int uid, Context context) {
        NetworkStatsManager networkStatsManager;
        float total;
        float receivedWifi = 0;
        float sentWifi = 0;
        float receivedMobData = 0;
        float sentMobData = 0;

        networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStats nwStatsWifi = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, null,
                startMillis, endMillis, uid);
        NetworkStats.Bucket bucketWifi = new NetworkStats.Bucket();
        while (nwStatsWifi.hasNextBucket()) {
            nwStatsWifi.getNextBucket(bucketWifi);
            receivedWifi = receivedWifi + bucketWifi.getRxBytes();
            sentWifi = sentWifi + bucketWifi.getTxBytes();
        }

        NetworkStats nwStatsMobData = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_MOBILE, null,
                startMillis, endMillis, uid);
        NetworkStats.Bucket bucketMobData = new NetworkStats.Bucket();
        while (nwStatsMobData.hasNextBucket()) {
            nwStatsMobData.getNextBucket(bucketMobData);
            receivedMobData = receivedMobData + bucketMobData.getRxBytes();
            sentMobData = sentMobData + bucketMobData.getTxBytes();
        }
        total = (receivedWifi + sentWifi + receivedMobData + sentMobData) / (1024 * 1024);

        DecimalFormat df = new DecimalFormat("00000");
        df.setRoundingMode(RoundingMode.DOWN);
        total = Float.parseFloat(df.format(total));

        return total;
    }
}
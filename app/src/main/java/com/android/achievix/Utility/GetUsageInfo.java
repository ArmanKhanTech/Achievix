package com.android.achievix.Utility;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetUsageInfo {
    Context context;

    public GetUsageInfo(Context context){
        this.context=context;
    }

    public float getPkgInfo(long startMillis, long endMillis, String pkgName){
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo info = null;
        int uid;
        try {
            info = packageManager.getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        uid = Objects.requireNonNull(info).uid;
        return fetchNetworkStatsInfo(startMillis, endMillis, uid);
    }

    public float fetchAppStatsInfo(long startMillis, long endMillis, String appPkg) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> lUsageStatsMap = usm.
                queryAndAggregateUsageStats(startMillis, endMillis);
        float total = 0.0f;
        if (lUsageStatsMap.containsKey(appPkg)) {
            total=getHours(Objects.requireNonNull(lUsageStatsMap.get(appPkg)).
                    getTotalTimeInForeground());
        }
        return total;
    }

    public float getRank(long startMillis, long endMillis, String appPkg) {
        int rank=0;
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> lUsageStatsMap = usm.
                queryAndAggregateUsageStats(startMillis, endMillis);
        List<UsageStats> queryUsageStats= new ArrayList<>() ;
        for(Map.Entry<String,UsageStats> stat: lUsageStatsMap.entrySet())
        {
            queryUsageStats.add(stat.getValue());
        }
        Collections.sort(queryUsageStats, new timeInForegroundComparator());
        for(UsageStats u:queryUsageStats){
            if(u.getPackageName().equals(appPkg)){
                break;
            }
            rank++;
        }
        rank=rank+1;
        DecimalFormat df=new DecimalFormat("00");
        df.setRoundingMode(RoundingMode.DOWN);
        df.format(rank);
        return rank;
    }

    private static class timeInForegroundComparator implements Comparator<UsageStats> {
        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }

    public float fetchNetworkStatsInfo(long startMillis, long endMillis, int uid) {
        NetworkStatsManager networkStatsManager;
        float total = 0.0f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        }
        DecimalFormat df=new DecimalFormat("00000");
        df.setRoundingMode(RoundingMode.DOWN);
        total= Float.parseFloat(df.format(total));
        return total;
    }

    private float getHours(long millis) {
        float seconds = (float) millis / 1000;
        float minutes = seconds / 60;
        float hours = (minutes/60);
        DecimalFormat df=new DecimalFormat("00.00");
        df.setRoundingMode(RoundingMode.DOWN);
        hours= Float.parseFloat(df.format(hours));
        return hours;
    }
}

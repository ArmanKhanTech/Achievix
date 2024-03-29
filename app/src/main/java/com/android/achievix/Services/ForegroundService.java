package com.android.achievix.Services;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.achievix.Activity.DrawOnTopAppActivity;
import com.android.achievix.Activity.DrawOnTopScreenActivity;
import com.android.achievix.Activity.EnterPasswordActivity;
import com.android.achievix.Activity.MainActivity;
import com.android.achievix.Database.AppLaunchDatabase;
import com.android.achievix.Database.InternetBlockDatabase;
import com.android.achievix.Database.LimitPackages;
import com.android.achievix.Database.RestrictPackages;
import com.android.achievix.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static String currentApp = "";
    public static String previousApp = "";
    public Context mContext;
    public RestrictPackages db = new RestrictPackages(this);
    public LimitPackages db1 = new LimitPackages(this);
    public AppLaunchDatabase appLaunchDatabase = new AppLaunchDatabase(this);
    public InternetBlockDatabase db3 = new InternetBlockDatabase(this);

    protected CountDownTimer check = new CountDownTimer(1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            // do nothing
        }

        @Override
        public void onFinish() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = sdf.format(new Date());

            UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

            long currentTime = System.currentTimeMillis();
            List<UsageStats> usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTime - 10000, currentTime);

            if (usageStats != null && !usageStats.isEmpty()) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStat : usageStats) {
                    mySortedMap.put(usageStat.getLastTimeUsed(), usageStat);
                }
                if (!mySortedMap.isEmpty()) {
                    previousApp = currentApp;
                    currentApp = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();

                    if (!previousApp.equals(currentApp)) {
                        appLaunchDatabase.incrementLaunchCount(currentApp, date);
                    }
                }
            }

            takeBreak(this);
            strictMode(this);

            ArrayList<String> packs = db.readRestrictPacks();
            ArrayList<String> packs1 = db1.readLimitPacks();
            ArrayList<String> packs2 = db3.readInternetPacks();

            if (packs.contains(currentApp)) {
                this.cancel();
                Intent lockIntent = new Intent(mContext, DrawOnTopAppActivity.class);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                lockIntent.putExtra("PACK_NAME", currentApp);
                String msg = "This App Is Blocked By FocusOnMe";
                lockIntent.putExtra("MSG", msg);
                startActivity(lockIntent);
            } else if (packs1.contains(currentApp)) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long startMillis;
                long endMillis;

                calendar.set(Calendar.HOUR_OF_DAY, 1);
                startMillis = calendar.getTimeInMillis();
                endMillis = System.currentTimeMillis();

                Map<String, UsageStats> lUsageStatsMap = usm.
                        queryAndAggregateUsageStats(startMillis, endMillis);

                String total = "";

                if (lUsageStatsMap.containsKey(currentApp)) {
                    long m = (Objects.requireNonNull(lUsageStatsMap.get(currentApp)).
                            getTotalTimeInForeground());
                    total = String.valueOf(m);
                }

                String compare = db1.readDuration(currentApp);

                long m1 = Long.parseLong(total);
                long m2 = Long.parseLong(compare);

                if (m1 > m2) {
                    this.cancel();
                    Intent lockIntent = new Intent(mContext, DrawOnTopAppActivity.class);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lockIntent.putExtra("PACK_NAME", currentApp);
                    String msg = "This App Is Blocked By FocusOnMe";
                    lockIntent.putExtra("MSG", msg);
                    startActivity(lockIntent);
                }
            } else if (packs2.contains(currentApp)) {
                String temp = db3.readData(currentApp);
                float data = Float.parseFloat(temp);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long startMillis;
                long endMillis;

                startMillis = calendar.getTimeInMillis();
                endMillis = System.currentTimeMillis();

                float currentData = getPkgInfo(startMillis, endMillis, currentApp);

                if (data < currentData) {
                    this.cancel();
                    Intent lockIntent = new Intent(mContext, DrawOnTopAppActivity.class);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lockIntent.putExtra("PACK_NAME", currentApp);
                    String msg = "This App Exceeds The Current Data Usage Limit";
                    lockIntent.putExtra("MSG", msg);
                    startActivity(lockIntent);
                }
            }
            this.start();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        check.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();

        SharedPreferences sh = getSharedPreferences("mode", Context.MODE_PRIVATE);
        int i = sh.getInt("password", 0);
        Intent notificationIntent;
        if (i != 0) {
            notificationIntent = new Intent(this, EnterPasswordActivity.class);
            notificationIntent.putExtra("password", i);
            notificationIntent.putExtra("invokedFrom", "ForegroundService");
        } else {
            notificationIntent = new Intent(this, MainActivity.class);
        }

        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Achievix")
                .setContentText(input)
                .setSmallIcon(R.drawable.noti_icon)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "AchievixForegroundServiceChannel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    public void strictMode(CountDownTimer timer) {
        SharedPreferences sh = getSharedPreferences("mode", MODE_PRIVATE);
        boolean _strict = sh.getBoolean("strict", false);

        if(_strict) {
            int level = sh.getInt("level", 0);
            switch (level) {
                case 1, 2, 3:
                    break;
                case 4:
                    if (currentApp.equals("com.android.settings")) {
                        timer.cancel();
                        Intent lockIntent = new Intent(mContext, DrawOnTopAppActivity.class);
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        lockIntent.putExtra("packageName", currentApp);
                        startActivity(lockIntent);
                    }
            }
        }
    }

    public void takeBreak(CountDownTimer timer) {
        SharedPreferences sh = getSharedPreferences("takeBreak", Context.MODE_PRIVATE);
        if(!sh.getBoolean("call", false)){
            if(sh.getInt("hour", 0) >= Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                    sh.getInt("minute", 0) >= Calendar.getInstance().get(Calendar.MINUTE)) {
                timer.cancel();
                Intent lockIntent = new Intent(mContext, DrawOnTopScreenActivity.class);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                lockIntent.putExtra("hour", sh.getInt("hour", 0));
                lockIntent.putExtra("minute", sh.getInt("minute", 0));
                lockIntent.putExtra("stop", sh.getBoolean("stop", false));
                lockIntent.putExtra("call", sh.getBoolean("call", false));
                lockIntent.putExtra("notification", sh.getBoolean("notification", false));
                startActivity(lockIntent);
            } else if(sh.getInt("hour", 0) <= Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                    sh.getInt("minute", 0) <= Calendar.getInstance().get(Calendar.MINUTE)) {
                SharedPreferences.Editor editor = sh.edit();
                editor.putInt("hour", 0);
                editor.putInt("minute", 0);
                editor.putBoolean("stop", false);
                editor.putBoolean("call", false);
                editor.putBoolean("notification", false);
                editor.apply();
            }
        } else {
            if(!currentApp.contains("dialer")){
                if(sh.getInt("hour", 0) >= Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                        sh.getInt("minute", 0) >= Calendar.getInstance().get(Calendar.MINUTE)) {
                    timer.cancel();
                    Intent lockIntent = new Intent(mContext, DrawOnTopScreenActivity.class);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lockIntent.putExtra("hour", sh.getInt("hour", 0));
                    lockIntent.putExtra("minute", sh.getInt("minute", 0));
                    lockIntent.putExtra("stop", sh.getBoolean("stop", false));
                    lockIntent.putExtra("call", sh.getBoolean("call", false));
                    lockIntent.putExtra("notification", sh.getBoolean("notification", false));
                    startActivity(lockIntent);
                } else if(sh.getInt("hour", 0) <= Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                        sh.getInt("minute", 0) <= Calendar.getInstance().get(Calendar.MINUTE)) {
                    SharedPreferences.Editor editor = sh.edit();
                    editor.putInt("hour", 0);
                    editor.putInt("minute", 0);
                    editor.putBoolean("stop", false);
                    editor.putBoolean("call", false);
                    editor.putBoolean("notification", false);
                    editor.apply();
                }
            }
        }
    }

    public float getPkgInfo(long startMillis, long endMillis, String packageName) {
        PackageManager packageManager = this.getPackageManager();
        ApplicationInfo info = null;

        try {
            info = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        }
        int uid = Objects.requireNonNull(info).uid;
        return fetchNetworkStatsInfo(startMillis, endMillis, uid);
    }

    public float fetchNetworkStatsInfo(long startMillis, long endMillis, int uid) {
        NetworkStatsManager networkStatsManager;
        float total;
        float receivedWifi = 0;
        float sentWifi = 0;
        float receivedMobData = 0;
        float sentMobData = 0;

        networkStatsManager = (NetworkStatsManager) this.getSystemService(Context.NETWORK_STATS_SERVICE);
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
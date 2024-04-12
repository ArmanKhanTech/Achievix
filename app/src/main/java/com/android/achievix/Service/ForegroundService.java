package com.android.achievix.Service;

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

import com.android.achievix.Activity.DrawOnTopLaunchActivity;
import com.android.achievix.Activity.DrawOnTopScreenActivity;
import com.android.achievix.Activity.EnterPasswordActivity;
import com.android.achievix.Activity.MainActivity;
import com.android.achievix.Database.AppLaunchDatabase;
import com.android.achievix.Database.BlockDatabase;
import com.android.achievix.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class ForegroundService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static String currentApp = "";
    private final AppLaunchDatabase appLaunchDatabase;
    private final BlockDatabase blockDatabase;
    private Context mContext;
    protected CountDownTimer check = new CountDownTimer(1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            updateCurrentApp();
            takeBreak(this);
            strictMode(this);
            block(this);
            this.start();
        }
    };

    public ForegroundService() {
        appLaunchDatabase = new AppLaunchDatabase(this);
        blockDatabase = new BlockDatabase(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        check.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForegroundService();
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

    private void startForegroundService() {
        SharedPreferences sh = getSharedPreferences("mode", Context.MODE_PRIVATE);
        int i = sh.getInt("password", 0);

        Intent notificationIntent = i != 0 ? new Intent(this, EnterPasswordActivity.class) : new Intent(this, MainActivity.class);
        notificationIntent.putExtra("password", i);
        notificationIntent.putExtra("invokedFrom", "ForegroundService");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Achievix")
                .setContentText("Foreground Service Running")
                .setSmallIcon(R.drawable.noti_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    private void updateCurrentApp() {
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
                String previousApp = currentApp;
                currentApp = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();

                if (!previousApp.equals(currentApp)) {
                    appLaunchDatabase.incrementLaunchCount(currentApp, date);
                }
            }
        }
    }

    public void block(CountDownTimer timer) {
        List<HashMap<String, String>> list = blockDatabase.readRecordsApp(currentApp);
        if(!list.isEmpty()) {
            Calendar calender = Calendar.getInstance();
            int currentDay = calender.get(Calendar.DAY_OF_WEEK);
            String currentDayName = getDay(currentDay);

            List<HashMap<String, String>> filteredList = new ArrayList<>();
            for (HashMap<String, String> map : list) {
                if (Objects.requireNonNull(map.get("scheduleDays")).contains(currentDayName)) {
                    filteredList.add(map);
                }
            }

            for (HashMap<String, String> map : filteredList) {
                if(Objects.equals(map.get("packageName"), currentApp)) {
                    if (Objects.equals(map.get("scheduleType"), "Usage Time") && Objects.equals(map.get("profileStatus"), "1")) {
                        if (Objects.equals(map.get("notification"), "1")) {
                            SharedPreferences sh = getSharedPreferences("notificationBlock", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putBoolean("notification", true);
                            editor.apply();
                        }

                        if (Objects.equals(map.get("launch"), "1")) {
                            String[] params = Objects.requireNonNull(map.get("scheduleParams")).split(" ");
                            int hour = Integer.parseInt(params[0]);
                            int minute = Integer.parseInt(params[1]);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            Log.d("Usage Time", "Start Time: " + calendar.getTime());

                            long startMillis = calendar.getTimeInMillis();
                            long endMillis = System.currentTimeMillis();

                            UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
                            Map<String, UsageStats> aggregatedStatsMap = usageStatsManager.queryAndAggregateUsageStats(startMillis, endMillis);
                            UsageStats usageStats = aggregatedStatsMap.get(currentApp);

                            if (usageStats != null) {
                                long time = usageStats.getTotalTimeInForeground();
                                long timeInMinutes = time / 60000;

                                if (timeInMinutes >= (hour * 60L + minute)) {
                                    timer.cancel();
                                    System.gc();
                                    Runtime.getRuntime().runFinalization();

                                    Intent lockIntent = new Intent(mContext, DrawOnTopLaunchActivity.class);
                                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    lockIntent.putExtra("packageName", currentApp);
                                    lockIntent.putExtra("type", map.get("type"));
                                    lockIntent.putExtra("text", map.get("text"));
                                    startActivity(lockIntent);
                                }
                            } else {
                                return;
                            }
                        }
                    } else if (Objects.equals(map.get("scheduleType"), "Specific Time") && Objects.equals(map.get("profileStatus"), "1")) {
                        if (Objects.equals(map.get("notification"), "1")) {
                            SharedPreferences sh = getSharedPreferences("notificationBlock", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putBoolean("notification", true);
                            editor.apply();
                        }

                        if (Objects.equals(map.get("launch"), "1")) {
                            String[] params = Objects.requireNonNull(map.get("scheduleParams")).split(" ");
                            int fromHours = Integer.parseInt(params[0]);
                            int fromMinutes = Integer.parseInt(params[1]);
                            int toHours = Integer.parseInt(params[2]);
                            int toMinutes = Integer.parseInt(params[3]);

                            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= fromHours &&
                                    Calendar.getInstance().get(Calendar.MINUTE) >= fromMinutes &&
                                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= toHours &&
                                    Calendar.getInstance().get(Calendar.MINUTE) <= toMinutes) {
                                timer.cancel();
                                System.gc();
                                Runtime.getRuntime().runFinalization();

                                Intent lockIntent = new Intent(mContext, DrawOnTopLaunchActivity.class);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                lockIntent.putExtra("packageName", currentApp);
                                lockIntent.putExtra("type", map.get("type"));
                                lockIntent.putExtra("text", map.get("text"));
                                startActivity(lockIntent);
                            } else {
                                return;
                            }
                        }
                    } else if (Objects.equals(map.get("scheduleType"), "Quick Block") && Objects.equals(map.get("profileStatus"), "1")) {
                        if (Objects.equals(map.get("notification"), "1")) {
                            SharedPreferences sh = getSharedPreferences("notificationBlock", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putBoolean("notification", true);
                            editor.apply();
                        }

                        if (Objects.equals(map.get("launch"), "1")) {
                            String[] params = Objects.requireNonNull(map.get("scheduleParams")).split(" ");
                            int untilHours = Integer.parseInt(params[0]);
                            int untilMins = Integer.parseInt(params[1]);

                            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= untilHours &&
                                    Calendar.getInstance().get(Calendar.MINUTE) <= untilMins) {
                                timer.cancel();
                                System.gc();
                                Runtime.getRuntime().runFinalization();

                                Intent lockIntent = new Intent(mContext, DrawOnTopLaunchActivity.class);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                lockIntent.putExtra("packageName", currentApp);
                                lockIntent.putExtra("type", map.get("type"));
                                lockIntent.putExtra("text", map.get("text"));
                                startActivity(lockIntent);
                            } else {
                                blockDatabase.deleteRecordById(map.get("id"));
                                return;
                            }
                        }
                    } else if (Objects.equals(map.get("scheduleType"), "Launch Count") && Objects.equals(map.get("profileStatus"), "1")) {
                        if (Objects.equals(map.get("notification"), "1")) {
                            SharedPreferences sh = getSharedPreferences("notificationBlock", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putBoolean("notification", true);
                            editor.apply();
                        }

                        if (Objects.equals(map.get("launch"), "1")) {
                            String[] params = Objects.requireNonNull(map.get("scheduleParams")).split(" ");
                            int launchCount = Integer.parseInt(params[0]);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String date = sdf.format(new Date());
                            int dailyCount = appLaunchDatabase.getDailyLaunchCountForSpecificApp(currentApp, date);

                            if (dailyCount >= launchCount) {
                                timer.cancel();
                                System.gc();
                                Runtime.getRuntime().runFinalization();

                                Intent lockIntent = new Intent(mContext, DrawOnTopLaunchActivity.class);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                lockIntent.putExtra("packageName", currentApp);
                                lockIntent.putExtra("type", map.get("type"));
                                lockIntent.putExtra("text", map.get("text"));
                                startActivity(lockIntent);
                            } else {
                                return;
                            }
                        }
                    } else if (Objects.equals(map.get("scheduleType"), "Fixed Block") && Objects.equals(map.get("profileStatus"), "1")) {
                        if (Objects.equals(map.get("notification"), "1")) {
                            SharedPreferences sh = getSharedPreferences("notificationBlock", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putBoolean("notification", true);
                            editor.apply();
                        }

                        if (Objects.equals(map.get("launch"), "1")) {
                            timer.cancel();
                            System.gc();
                            Runtime.getRuntime().runFinalization();

                            Intent lockIntent = new Intent(mContext, DrawOnTopLaunchActivity.class);
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            lockIntent.putExtra("packageName", currentApp);
                            lockIntent.putExtra("type", map.get("type"));
                            lockIntent.putExtra("text", map.get("text"));
                            startActivity(lockIntent);
                        }
                    } else if (Objects.equals(map.get("scheduleType"), "Block Data")) {
                        String[] params = Objects.requireNonNull(map.get("scheduleParams")).split(" ");
                        int usage = Integer.parseInt(params[0]);
                        boolean mobile = Objects.equals(map.get("launch"), "1");
                        boolean wifi = Objects.equals(map.get("notification"), "1");

                        float total = getPackageInfo(0, System.currentTimeMillis(), currentApp, mobile, wifi);
                        if (total >= usage) {
                            timer.cancel();
                            System.gc();
                            Runtime.getRuntime().runFinalization();

                            Intent lockIntent = new Intent(mContext, DrawOnTopLaunchActivity.class);
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            lockIntent.putExtra("packageName", currentApp);
                            lockIntent.putExtra("type", map.get("type"));
                            lockIntent.putExtra("text", map.get("text"));
                            startActivity(lockIntent);
                        } else {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
        }
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
                        Intent lockIntent = new Intent(mContext, DrawOnTopLaunchActivity.class);
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        lockIntent.putExtra("packageName", currentApp);
                        lockIntent.putExtra("type", "app");
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

    private String getDay(int day) {
        return switch (day) {
            case Calendar.SUNDAY -> "Sunday";
            case Calendar.MONDAY -> "Monday";
            case Calendar.TUESDAY -> "Tuesday";
            case Calendar.WEDNESDAY -> "Wednesday";
            case Calendar.THURSDAY -> "Thursday";
            case Calendar.FRIDAY -> "Friday";
            case Calendar.SATURDAY -> "Saturday";
            default -> "";
        };
    }

    public float getPackageInfo(long startMillis, long endMillis, String packageName, boolean mobile, boolean wifi) {
        PackageManager packageManager = this.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        int uid = Objects.requireNonNull(info).uid;
        return fetchNetworkStatsInfo(startMillis, endMillis, uid, mobile, wifi);
    }

    public float fetchNetworkStatsInfo(long startMillis, long endMillis, int uid, boolean mobile, boolean wifi) {
        float total;
        float receivedWifi = 0;
        float sentWifi = 0;
        float receivedMobData = 0;
        float sentMobData = 0;

        NetworkStatsManager networkStatsManager = (NetworkStatsManager) this.getSystemService(Context.NETWORK_STATS_SERVICE);

        if (wifi) {
            NetworkStats nwStatsWifi = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, null,
                    startMillis, endMillis, uid);
            NetworkStats.Bucket bucketWifi = new NetworkStats.Bucket();
            while (nwStatsWifi.hasNextBucket()) {
                nwStatsWifi.getNextBucket(bucketWifi);
                receivedWifi = receivedWifi + bucketWifi.getRxBytes();
                sentWifi = sentWifi + bucketWifi.getTxBytes();
            }
        }

        if (mobile) {
            NetworkStats nwStatsMobData = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_MOBILE, null,
                    startMillis, endMillis, uid);
            NetworkStats.Bucket bucketMobData = new NetworkStats.Bucket();
            while (nwStatsMobData.hasNextBucket()) {
                nwStatsMobData.getNextBucket(bucketMobData);
                receivedMobData = receivedMobData + bucketMobData.getRxBytes();
                sentMobData = sentMobData + bucketMobData.getTxBytes();
            }
        }

        total = (receivedWifi + sentWifi + receivedMobData + sentMobData) / (1024 * 1024);

        DecimalFormat df = new DecimalFormat("00000");
        df.setRoundingMode(RoundingMode.DOWN);
        total = Float.parseFloat(df.format(total));

        return total;
    }
}
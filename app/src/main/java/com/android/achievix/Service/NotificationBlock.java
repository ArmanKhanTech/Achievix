package com.android.achievix.Service;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.Database.LimitPackages;
import com.android.achievix.Database.RestrictPackages;

import java.util.ArrayList;

public class NotificationBlock extends NotificationListenerService {
    private LimitPackages limitPackagesDb;
    private RestrictPackages restrictPackagesDb;
    private AnalysisDatabase analysisDatabaseDb;

    @Override
    public void onCreate() {
        super.onCreate();

        limitPackagesDb = new LimitPackages(this);
        restrictPackagesDb = new RestrictPackages(this);
        analysisDatabaseDb = new AnalysisDatabase(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        limitPackagesDb.close();
        restrictPackagesDb.close();
        analysisDatabaseDb.close();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        ArrayList<String> limitPacks =limitPackagesDb.readLimitPacks();
        ArrayList<String> restrictPacks = restrictPackagesDb.readRestrictPacks();

        if (limitPacks.contains(sbn.getPackageName()) || restrictPacks.contains(sbn.getPackageName())) {
            cancelNotification(sbn.getKey());
            analysisDatabaseDb.inAppNotiblocked(sbn.getPackageName());
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // Release any unnecessary resources to help reduce your service's memory footprint.
    }
}
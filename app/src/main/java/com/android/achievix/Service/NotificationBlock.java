package com.android.achievix.Service;
//DONE
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.android.achievix.DataBase.AnalysisDatabase;
import com.android.achievix.DataBase.SaveLimitPackages;
import com.android.achievix.DataBase.SaveRestrictPackages;

import java.util.ArrayList;

public class NotificationBlock extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        SaveLimitPackages db = new SaveLimitPackages(this);
        SaveRestrictPackages db1 = new SaveRestrictPackages(this);
        ArrayList<String> packs = db1.readRestrictPacks();
        ArrayList<String> packs1 = db.readLimitPacks();
        AnalysisDatabase db2=new AnalysisDatabase(this);
        if(db.isDbEmpty()) {
            if (packs.contains(sbn.getPackageName())) {
                cancelNotification(sbn.getKey());
                db2.inAppNotiblocked(sbn.getPackageName());
            }
        }
        if(db1.isDbEmpty()) {
            if (packs1.contains(sbn.getPackageName())) {
                cancelNotification(sbn.getKey());
                db2.inAppNotiblocked(sbn.getPackageName());
            }
        }
    }
}

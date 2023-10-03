package com.android.achievix.Service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.android.achievix.Activity.BlockWebActivity;
import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.Database.BlockWebsite;

import java.util.ArrayList;
import java.util.List;

public class LogURLService extends AccessibilityService {

    public String browserApp="";
    public String browserUrl="";
    BlockWebsite db;
    ArrayList<String> blockedUrls;

    @SuppressLint("SwitchIntDef")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        final int eventType = event.getEventType();

        switch(eventType) {

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: {
                AccessibilityNodeInfo parentNodeInfo = event.getSource();
                if (parentNodeInfo == null) {
                    return;
                }

                String packageName;
                event.getPackageName().toString();
                packageName = event.getPackageName().toString();
                SupportedBrowserConfig browserConfig = null;
                for (SupportedBrowserConfig supportedConfig: getSupportedBrowsers()) {
                    if (supportedConfig.packageName.equals(packageName)) {
                        browserConfig = supportedConfig;
                    }
                }

                if (browserConfig == null) {
                    return;
                }

                String capturedUrl = captureUrl(parentNodeInfo, browserConfig);
                parentNodeInfo.recycle();

                if (capturedUrl == null) {
                    return;
                }

                AnalysisDatabase db2=new AnalysisDatabase(this);

                if(!packageName.equals(browserApp))
                {
                    if(android.util.Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                        browserUrl = capturedUrl;
                        browserApp = packageName;
                        String url="";
                        if(browserUrl.length()>0) {
                            if (browserUrl.contains("/")) {
                                url = browserUrl.substring(0, browserUrl.indexOf("/"));
                            } else {
                                if(!browserUrl.contains(" ")) {
                                    url = browserUrl;
                                }
                            }
                            db = new BlockWebsite(this);
                            blockedUrls = db.readWebsites();
                            if (blockedUrls.contains(url)) {
                                Intent lockIntent = new Intent(this, BlockWebActivity.class);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                lockIntent.putExtra("URL", url);
                                lockIntent.putExtra("PACKAGE", browserApp);
                                db2.inWebAccessDenied(url);
                                startActivity(lockIntent);
                            }
                        }
                    }
                }
                else
                {
                    if(!capturedUrl.equals(browserUrl))
                    {
                        if(android.util.Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                            browserUrl = capturedUrl;
                            String url = "";
                            if(browserUrl.length()>0) {
                                if (browserUrl.contains("/")) {
                                    url = browserUrl.substring(0, browserUrl.indexOf("/"));
                                } else {
                                    if(!browserUrl.contains(" ")) {
                                        url = browserUrl;
                                    }
                                }
                                db = new BlockWebsite(this);
                                blockedUrls = db.readWebsites();
                                if (blockedUrls.contains(url)) {
                                    Intent lockIntent = new Intent(this, BlockWebActivity.class);
                                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    lockIntent.putExtra("URL", url);
                                    lockIntent.putExtra("PACKAGE", browserApp);
                                    db2.inWebAccessDenied(url);
                                    startActivity(lockIntent);
                                }
                            }
                        }
                    }
                }
            }
            break;
        }
    }

    private static class SupportedBrowserConfig {
        public String packageName, addressBarId;
        public SupportedBrowserConfig(String packageName, String addressBarId) {
            this.packageName = packageName;
            this.addressBarId = addressBarId;
        }
    }

    @NonNull
    private static List<SupportedBrowserConfig> getSupportedBrowsers() {
        List<SupportedBrowserConfig> browsers = new ArrayList<>();
        browsers.add( new SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"));
        browsers.add( new SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"));
        browsers.add( new SupportedBrowserConfig("com.opera.browser", "com.opera.browser:id/url_field"));
        browsers.add( new SupportedBrowserConfig("com.opera.mini.native", "com.opera.mini.native:id/url_field"));

        return browsers;
    }

    private String captureUrl(AccessibilityNodeInfo info, SupportedBrowserConfig config) {
        List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByViewId(config.addressBarId);
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        AccessibilityNodeInfo addressBarNodeInfo = nodes.get(0);
        String url = null;
        if (addressBarNodeInfo.getText() != null) {
            url = addressBarNodeInfo.getText().toString();
        }
        addressBarNodeInfo.refresh();
        return url;
    }

    @Override
    public void onInterrupt() {

    }
    @Override
    public void onServiceConnected() {

    }
}

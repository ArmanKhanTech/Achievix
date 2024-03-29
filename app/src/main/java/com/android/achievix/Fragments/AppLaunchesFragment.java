package com.android.achievix.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.achievix.Adapter.AppLaunchAdapter;
import com.android.achievix.Database.AppLaunchDatabase;
import com.android.achievix.Model.AppUsageModel;
import com.android.achievix.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AppLaunchesFragment extends Fragment {
    RecyclerView recyclerView;
    List<AppUsageModel> appLaunchModel = new ArrayList<>();
    TextView stats;
    String[] sort = {"Daily", "Weekly", "Monthly", "Yearly"};
    String sortValue = "Daily";
    private LinearLayout llUsageOverview;
    private LinearLayout loadingLayout;
    AppLaunchDatabase appLaunchDatabase;
    HashMap<String, Integer> appLaunchCount;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String date = sdf.format(new Date());
    String startDate = "";
    Calendar calendar = Calendar.getInstance();
    int totalCount = 0;
    private GetAppLaunchCountTask getAppLaunchCountTask;

    public AppLaunchesFragment() {
        // do nothing
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_app_launches, container, false);

        Spinner spinner = view.findViewById(R.id.app_launch_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.spinner_dropdown_item, sort);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    boolean isFirstTime = true;

                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (isFirstTime) {
                            isFirstTime = false;
                        } else {
                            sortValue = parent.getItemAtPosition(position).toString();
                            new GetAppLaunchCountTask(requireActivity(), sortValue).execute();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // do nothing
                    }
                }
        );

        loadingLayout = view.findViewById(R.id.loading_app_launches);
        llUsageOverview = view.findViewById(R.id.ll_app_launches);
        stats = view.findViewById(R.id.tv_app_launches_count);

        recyclerView = view.findViewById(R.id.recycler_view_app_launches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        getAppLaunchCountTask = new GetAppLaunchCountTask(requireActivity(), sortValue);
        getAppLaunchCountTask.execute();

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAppLaunchCountTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final String sort;

        public GetAppLaunchCountTask(Context context, String sort) {
            this.context = context;
            this.sort = sort;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingLayout.setVisibility(View.VISIBLE);
            llUsageOverview.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appLaunchDatabase = new AppLaunchDatabase(context);
            switch (sort) {
                case "Daily":
                    appLaunchCount = appLaunchDatabase.getDailyLaunchCount(date);
                    break;
                case "Weekly":
                    calendar.add(Calendar.DAY_OF_MONTH, -30);
                    startDate = sdf.format(calendar.getTime());
                    appLaunchCount = appLaunchDatabase.getWeeklyLaunchCount(startDate, date);
                    break;
                case "Monthly":
                    calendar.add(Calendar.MONTH, -1);
                    startDate = sdf.format(calendar.getTime());
                    appLaunchCount = appLaunchDatabase.getMonthlyLaunchCount(startDate, date);
                    break;
                case "Yearly":
                    calendar.add(Calendar.YEAR, -1);
                    startDate = sdf.format(calendar.getTime());
                    appLaunchCount = appLaunchDatabase.getYearlyLaunchCount(startDate, date);
                    break;
            }

            List<String> packageNames = new ArrayList<>(appLaunchCount.keySet());
            PackageManager pm = context.getPackageManager();

            totalCount = 0;
            for (String packageName : packageNames) {
                if (appLaunchCount.get(packageName) != null && appLaunchCount.get(packageName) > 0 && !packageName.isEmpty()){
                    totalCount += appLaunchCount.get(packageName);
                }
            }

            appLaunchModel.clear();
            for (String packageName : packageNames) {
                try {
                    if (appLaunchCount.get(packageName) != null && appLaunchCount.get(packageName) > 0 && !packageName.isEmpty()){
                        ApplicationInfo appInfo = pm.getPackageInfo(packageName, 0).applicationInfo;
                        String appName = appInfo.loadLabel(pm).toString();
                        Drawable appIcon = appInfo.loadIcon(pm);
                        Bitmap bitmap = Bitmap.createBitmap(appIcon.getIntrinsicWidth(), appIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        appIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        appIcon.draw(canvas);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        appIcon = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                        int launchCount = appLaunchCount.get(packageName);
                        double progress = (double) launchCount / totalCount * 100;
                        appLaunchModel.add(new AppUsageModel(appName, packageName, appIcon, String.valueOf(launchCount), progress));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // do nothing
                }
            }

            appLaunchModel.sort((o1, o2) -> Integer.parseInt(Objects.requireNonNull(o2.getExtra())) - Integer.parseInt(Objects.requireNonNull(o1.getExtra())));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isCancelled()) {
                return;
            }
            stats.setText(String.valueOf(totalCount));
            recyclerView.setAdapter(new AppLaunchAdapter(appLaunchModel));
            loadingLayout.setVisibility(View.GONE);
            llUsageOverview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getAppLaunchCountTask != null) {
            getAppLaunchCountTask.cancel(true);
        }
    }
}
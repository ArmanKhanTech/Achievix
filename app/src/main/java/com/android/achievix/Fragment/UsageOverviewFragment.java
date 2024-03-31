package com.android.achievix.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.android.achievix.Adapter.AppUsageAdapter;
import com.android.achievix.Model.AppUsageModel;
import com.android.achievix.R;
import com.android.achievix.Utility.UsageUtil;

import java.util.List;

public class UsageOverviewFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView usageStats;
    private LinearLayout usageLayout;
    private LinearLayout loadingLayout;
    private Spinner sortSpinner;
    private final String[] sort = {"Daily", "Weekly", "Monthly", "Yearly"};
    private String sortValue = "Daily";
    private GetInstalledAppsUsageTask getInstalledAppsUsageTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usage_overview, container, false);
        initializeViews(view);
        setupSpinner();
        setupRecyclerView();
        getInstalledAppsUsageTask = new GetInstalledAppsUsageTask(requireActivity(), sortValue);
        getInstalledAppsUsageTask.execute();
        return view;
    }

    private void initializeViews(View view) {
        usageStats = view.findViewById(R.id.tv_total_usage_overview);
        recyclerView = view.findViewById(R.id.recycler_view_usage_overview);
        sortSpinner = view.findViewById(R.id.usage_spinner);
        loadingLayout = view.findViewById(R.id.loading_usage_overview);
        usageLayout = view.findViewById(R.id.ll_usage_overview);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.spinner_dropdown_item, sort);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstTime) {
                    sortValue = parent.getItemAtPosition(position).toString();
                    new GetInstalledAppsUsageTask(requireActivity(), sortValue).execute();
                }
                isFirstTime = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private static String convertMillisToHoursAndMinutes(long millis) {
        long hours = millis / 1000 / 60 / 60;
        long minutes = (millis / 1000 / 60) % 60;
        return hours + " hrs " + minutes + " mins";
    }

    @SuppressLint("StaticFieldLeak")
    private class GetInstalledAppsUsageTask extends AsyncTask<Void, Void, List<AppUsageModel>> {
        private final Context context;
        private final String sort;

        GetInstalledAppsUsageTask(Context context, String sort) {
            this.context = context;
            this.sort = sort;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingLayout.setVisibility(View.VISIBLE);
            usageLayout.setVisibility(View.GONE);
        }

        @Override
        protected List<AppUsageModel> doInBackground(Void... voids) {
            return UsageUtil.Companion.getInstalledAppsUsage(context, sort);
        }

        @Override
        protected void onPostExecute(List<AppUsageModel> result) {
            if (isCancelled()) {
                return;
            }
            usageStats.setText(convertMillisToHoursAndMinutes(UsageUtil.totalUsage));
            recyclerView.setAdapter(new AppUsageAdapter(result));
            loadingLayout.setVisibility(View.GONE);
            usageLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getInstalledAppsUsageTask != null) {
            getInstalledAppsUsageTask.cancel(true);
        }
    }
}
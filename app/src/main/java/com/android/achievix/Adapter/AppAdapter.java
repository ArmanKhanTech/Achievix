package com.android.achievix.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.achievix.DataBase.Packages;
import com.android.achievix.R;

import java.util.ArrayList;

public class AppAdapter extends BaseAdapter implements Filterable {

    public LayoutInflater layoutInflater;
    public ArrayList<AppList> listStorage;
    public ArrayList<AppList> filteredApps;
    public ArrayList<String> packs;
    Packages db;
    Context mContext;
    ItemFilter cs;

    public AppAdapter(Context context, ArrayList<AppList> customizedListView) {
        layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
        filteredApps= customizedListView;
        db=new Packages(context);
        mContext=context;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public AppList getItem(int position) {
        return listStorage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.app_list, parent, false);
            listViewHolder.textInListView = convertView.findViewById(R.id.app_name);
            listViewHolder.imageInListView = convertView.findViewById(R.id.app_icon);
            listViewHolder.extra = convertView.findViewById(R.id.extra);
            listViewHolder.cs=convertView.findViewById(R.id.ConstraintLayout);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        listViewHolder.textInListView.setText(listStorage.get(position).getName());
        listViewHolder.imageInListView.setImageDrawable(listStorage.get(position).getIcon());
        listViewHolder.extra.setText(listStorage.get(position).getExtra());
        packs= db.readPacks();
        if(!packs.contains(listStorage.get(position).getPackageName())){
            listViewHolder.cs.setBackgroundResource(R.drawable.listview_theme);
        }
        else{
            listViewHolder.cs.setBackgroundResource(R.drawable.listview_select_theme);
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(cs == null){
            cs =new ItemFilter();
        }
        return cs;
    }

    class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();

            if(constraint!=null && constraint.length()>0) {
                constraint = constraint.toString().toUpperCase();
                ArrayList<AppList> filters = new ArrayList<>();
                int a;
                for (a = 0; a < filteredApps.size(); a++) {
                    if (filteredApps.get(a).getName().toUpperCase().contains(constraint)) {
                        AppList appList = new AppList(filteredApps.get(a).getName(), filteredApps.get(a).getPackageName(),filteredApps.get(a).getIcon());
                        filters.add(appList);
                    }
                }
                results.count=filters.size();
                results.values=filters;
            }
            else{
                results.count=filteredApps.size();
                results.values=filteredApps;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            listStorage=(ArrayList<AppList>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
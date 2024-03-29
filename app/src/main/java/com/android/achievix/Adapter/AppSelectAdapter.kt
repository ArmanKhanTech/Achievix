package com.android.achievix.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.achievix.Model.AppSelectModel
import com.android.achievix.R

class AppSelectAdapter(private var appList: List<AppSelectModel>) :
    RecyclerView.Adapter<AppSelectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.app_name_select)
        val appIcon: ImageView = view.findViewById(R.id.app_icon_select)
        val extra: TextView = view.findViewById(R.id.app_info_select)
        val checkBox: CheckBox = view.findViewById(R.id.app_select_cb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.app_list_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = appList[position]

        holder.appName.text = appInfo.name
        holder.appIcon.setImageDrawable(appInfo.icon)
        holder.extra.text = convertToHrsMins(appInfo.extra.toLong())
        holder.checkBox.isChecked = appInfo.selected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            appInfo.selected = isChecked
            holder.checkBox.isChecked = isChecked
        }

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = appInfo.selected
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            appInfo.selected = isChecked
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListSelect(newList: List<AppSelectModel>) {
        appList = newList
        notifyDataSetChanged()
    }

    private fun convertToHrsMins(millis: Long): String {
        val hours = millis / 1000 / 60 / 60
        val minutes = millis / 1000 / 60 % 60
        return "$hours hrs $minutes mins"
    }

    override fun getItemCount() = appList.size
}
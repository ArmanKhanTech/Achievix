package com.android.achievix.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View.GONE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.achievix.Adapter.OnTimeClickListener
import com.android.achievix.Adapter.TimeLineAdapter
import com.android.achievix.Model.TimeLineModel
import com.android.achievix.R
import com.android.achievix.Services.AdminReceiver
import com.android.achievix.Utility.ItemStatus
import java.util.Objects

class SelectStrictMode : AppCompatActivity() {
    private lateinit var adapter: TimeLineAdapter
    private val dataList = ArrayList<TimeLineModel>()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var sh: SharedPreferences
    private lateinit var cn: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_strict_mode)

        sh = getSharedPreferences("mode", MODE_PRIVATE)
        editor = sh.edit()

        val activate = findViewById<Button>(R.id.activate_strict_mode)
        if(!sh.getBoolean("strict", false)) {
            activate.setOnClickListener {
                if(dataList[2].status != ItemStatus.COMPLETED) {
                    Toast.makeText(this, "Please activate device admin first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    editor.putBoolean("strict", true)
                    editor.apply()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        } else {
            activate.visibility = GONE
        }

        cn = ComponentName(this, AdminReceiver::class.java)

        setDataListItems()
        initRecyclerView()
    }

    private fun setDataListItems() {
        dataList.add(
            TimeLineModel(
                "Blocking Level",
                "Select what all will be blocked",
                ItemStatus.ACTIVE
            )
        )
        dataList.add(
            TimeLineModel(
                "Set Password",
                "Required for accessing Achievix",
                ItemStatus.COMPLETED
            )
        )
        dataList.add(
            TimeLineModel(
                "Activate Device Admin",
                "Grant device admin rights to Achievix",
                ItemStatus.COMPLETED
            )
        )
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_mode_strict)
        recyclerView.layoutManager = layoutManager

        adapter = TimeLineAdapter(dataList, object : OnTimeClickListener {
            override fun onTimeClick(position: Int) {
                when (position) {
                    0 -> {
                        val dialog = Dialog(this@SelectStrictMode)
                        dialog.setContentView(R.layout.strict_level_dialog)
                        dialog.setCancelable(true)
                        Objects.requireNonNull(
                            dialog
                                .window
                        )
                        dialog.window?.setLayout(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        val one = dialog.findViewById<LinearLayout>(R.id.strict_level_one)
                        val two = dialog.findViewById<LinearLayout>(R.id.strict_level_two)
                        val three = dialog.findViewById<LinearLayout>(R.id.strict_level_three)
                        val four = dialog.findViewById<LinearLayout>(R.id.strict_level_four)

                        one.setOnClickListener {
                            if (dataList[0].status == ItemStatus.ACTIVE) {
                                editor.putInt("level", 1)
                                dataList[0].status = ItemStatus.COMPLETED
                                dataList[1].status = ItemStatus.ACTIVE
                                dataList[0].text = "Blocking Level set to One"
                                dialog.dismiss()
                                initRecyclerView()
                            } else {
                                Toast.makeText(this@SelectStrictMode, "Blocking Level already set to One", Toast.LENGTH_SHORT).show()
                            }
                        }

                        two.setOnClickListener {
                            if(dataList[0].status == ItemStatus.ACTIVE) {
                                editor.putInt("level", 2)
                                dataList[0].status = ItemStatus.COMPLETED
                                dataList[1].status = ItemStatus.ACTIVE
                                dataList[0].text = "Blocking Level set to Two"
                                dialog.dismiss()
                                initRecyclerView()
                            } else {
                                Toast.makeText(this@SelectStrictMode, "Blocking Level already set to Two", Toast.LENGTH_SHORT).show()
                            }
                        }

                        three.setOnClickListener {
                            if(dataList[0].status == ItemStatus.ACTIVE) {
                                editor.putInt("level", 3)
                                dataList[0].status = ItemStatus.COMPLETED
                                dataList[1].status = ItemStatus.ACTIVE
                                dataList[0].text = "Blocking Level set to Three"
                                dialog.dismiss()
                                initRecyclerView()
                            } else {
                                Toast.makeText(this@SelectStrictMode, "Blocking Level already set to Three", Toast.LENGTH_SHORT).show()
                            }
                        }

                        four.setOnClickListener {
                            if(dataList[0].status == ItemStatus.ACTIVE) {
                                editor.putInt("level", 4)
                                dataList[0].status = ItemStatus.COMPLETED
                                dataList[1].status = ItemStatus.ACTIVE
                                dataList[0].text = "Blocking Level set to Four"
                                dialog.dismiss()
                                initRecyclerView()
                            } else {
                                Toast.makeText(this@SelectStrictMode, "Blocking Level already set to Four", Toast.LENGTH_SHORT).show()
                            }
                        }

                        dialog.show()
                    }

                    1 -> {
                        if(dataList[0].status != ItemStatus.COMPLETED) {
                            Toast.makeText(this@SelectStrictMode, "Please select blocking level first", Toast.LENGTH_SHORT).show()
                            return
                        } else {
                            if(dataList[1].status == ItemStatus.ACTIVE) {
                                val intent = Intent(this@SelectStrictMode, NewPasswordActivity::class.java)
                                startActivityForResult(intent, 100)
                            } else {
                                Toast.makeText(this@SelectStrictMode, "Password already set", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    2 -> {
                        if(dataList[1].status != ItemStatus.COMPLETED) {
                            Toast.makeText(this@SelectStrictMode, "Please set password first", Toast.LENGTH_SHORT).show()
                            return
                        } else {
                            if(dataList[2].status == ItemStatus.ACTIVE) {
                                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn)
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Achievix requires device admin rights to restrict deletion of the app.")
                                startActivityForResult(intent, 101)
                            } else {
                                Toast.makeText(this@SelectStrictMode, "Device Admin already activated", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
        recyclerView.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                dataList[1].status = ItemStatus.COMPLETED
                dataList[2].status = ItemStatus.ACTIVE
                dataList[1].text = "Password set"
                initRecyclerView()
            }
        } else if(requestCode == 101) {
            if(resultCode == RESULT_OK) {
                dataList[2].status = ItemStatus.COMPLETED
                dataList[2].text = "Device Admin activated"
                initRecyclerView()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}
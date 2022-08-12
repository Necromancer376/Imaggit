package com.example.memeshare

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_starred_images.*

class StarredImagesActivity : AppCompatActivity() {

    private var starredItems = ArrayList<String>()
    private val keyStarred: String = "STARRED"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starred_images)

        setupActionBar()
        starredItems = getStarredArray(keyStarred)
        logStarred()

        rv_starred.layoutManager = GridLayoutManager(this@StarredImagesActivity, 2)
        rv_starred.setHasFixedSize(true)

        val adapter = StarredItemsAdapter(this@StarredImagesActivity, getStarredArray(keyStarred))
        rv_starred.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        starredItems = getStarredArray(keyStarred)
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_starred_images_activity)
        val actionbar = supportActionBar
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_back_button_image)
        }

        toolbar_starred_images_activity.setNavigationOnClickListener{
            onBackPressed()
            finish()
        }
    }

    fun saveStarredArray(list: ArrayList<String>, key: String?) {
        val sharedPreferences = getSharedPreferences("starredImages", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringArrayList(key!!, list)
        editor.apply()
    }

    fun getStarredArray(key: String?): ArrayList<String> {
        val sharedPreferences = getSharedPreferences("starredImages", Context.MODE_PRIVATE)
        val defaultArray = ArrayList<String>()

        return sharedPreferences.getStringArrayList(key!!, defaultArray)!!
    }

    fun SharedPreferences.Editor.putStringArrayList(key: String, list: ArrayList<String>?): SharedPreferences.Editor {
        putString(key, list?.joinToString(",") ?: "")
        return this
    }

    fun SharedPreferences.getStringArrayList(key: String, defValue: ArrayList<String>?): ArrayList<String>? {
        val value = getString(key, null)
        if (value.isNullOrBlank())
            return defValue
        return ArrayList (value.split(",").map { it })
    }

    fun logStarred() {
        for(i in 0..starredItems.size-1) {
            Log.e(i.toString(), starredItems[i])
        }
    }
}
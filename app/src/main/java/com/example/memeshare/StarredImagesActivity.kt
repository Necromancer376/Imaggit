package com.example.memeshare

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    fun getStarredArray(key: String?): ArrayList<String> {
        val sharedPreferences = getSharedPreferences("starredImages", Context.MODE_PRIVATE)
        val defaultArray = ArrayList<String>()

        return sharedPreferences.getStringArrayList(key!!, defaultArray)!!
    }

    fun SharedPreferences.getStringArrayList(key: String, defValue: ArrayList<String>?): ArrayList<String>? {
        val value = getString(key, null)
        if (value.isNullOrBlank())
            return defValue
        return ArrayList (value.split(",").map { it })
    }
}
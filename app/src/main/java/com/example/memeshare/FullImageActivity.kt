package com.example.memeshare

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_full_image.*


class FullImageActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    var currentImageUrl = ""
    var starredItems = ArrayList<String>()
    val keyStarred: String = "STARRED"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        setupActionBar()

        currentImageUrl = intent.getStringExtra("imgID").toString()

        Glide
            .with(this)
            .load(currentImageUrl)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(imgFull)

        starredItems = getStarredArray(keyStarred)

        btnRemove.setOnClickListener {
            starredItems.remove(currentImageUrl)
            saveStarredArray(starredItems, keyStarred)
            startActivity(Intent(this@FullImageActivity,  StarredImagesActivity::class.java))
            finish()
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_full_image_activity)
        val actionbar = supportActionBar
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_back_button_image)
        }

        toolbar_full_image_activity.setNavigationOnClickListener{ onBackPressed() }
    }

    @SuppressLint("CommitPrefEdits")
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

    // Gesture
    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent?) {
        TODO("Not yet implemented")
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
        TODO("Not yet implemented")
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }
}
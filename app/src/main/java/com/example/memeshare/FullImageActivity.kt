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
    var pos: Int = 0

    lateinit var gestureDetector: GestureDetector
    var x1: Float = 0.0f
    var x2: Float = 0.0f
    var y1: Float = 0.0f
    var y2: Float = 0.0f

    companion object {
        const val MIN_DISTANCE = 150
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        setupActionBar()
        gestureDetector = GestureDetector(this@FullImageActivity, this@FullImageActivity)

        starredItems = getStarredArray(keyStarred)

//        currentImageUrl = intent.getStringExtra("imgID").toString()
        pos = intent.getIntExtra("pos", 0)

        loadImg()

        btnRemove.setOnClickListener {
            starredItems.remove(currentImageUrl)
            saveStarredArray(starredItems, keyStarred)
            startActivity(Intent(this@FullImageActivity,  StarredImagesActivity::class.java))
            finish()
        }

    }

    private fun loadImg() {
        currentImageUrl = starredItems[pos]

        Glide
            .with(this)
            .load(currentImageUrl)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(imgFull)
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
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        when(event?.action) {
            0 -> {
                x1 = event.x
                y1 = event.y
            }
            1 -> {
                x2 = event.x
                y2 = event.y
                val valueX: Float = x2 - x1

                if(valueX* -1  > MIN_DISTANCE) {
                    if(pos < starredItems.size-1) {
                        pos++
                        loadImg()
                    }
                }
                else {
                    pos--
                    if(pos < 0) { pos = 0 }
                    loadImg()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent?) {

    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {

    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }
}
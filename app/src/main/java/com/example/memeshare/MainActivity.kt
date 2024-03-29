package com.example.memeshare

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.sharememes.MySingleton
import kotlinx.android.synthetic.main.activity_full_image.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.math.abs


class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener, View.OnClickListener {

    val keyStarred: String = "STARRED"
    var currentImageUrl: String? = null

    var subreddit = ""
    var starredItems = ArrayList<String>()
    var toggleStar = false
    var doubleTap = false

    var imgList: MutableList<String> = mutableListOf()
    var pointer = 0

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
        setContentView(R.layout.activity_main)

        setupActionBar()
        gestureDetector = GestureDetector(this@MainActivity, this@MainActivity)

        btnShare.setOnClickListener {
            shareMeme()
//            buttonEffect(btnShare)
        }
        btnNext.setOnClickListener {
            pointer = 0
            nextMeme()
//            Timer().schedule(2000) {
//                btnNext.setBackgroundResource(R.color.white) //set the color to black
//                Log.e("set", "background")
//            }
//            buttonEffect(btnNext)
        }

        btnChange.setOnClickListener {
            chageSubreddit()
//            buttonEffect(btnChange)
        }
        btnSeeStarred.setOnClickListener {
            startActivity(Intent(this@MainActivity,  StarredImagesActivity::class.java))
        }
        imgStar.setOnClickListener { addToStarred() }
        edtSubreddit.setSelectAllOnFocus(true)

        starredItems = getStarredArray(keyStarred)
        loadMeme()
    }

//    override fun onClick(v: View) {
//        var r = object: Runnable {
//            override fun run() { doubleTap = false }
//        }
//        if(doubleTap) {
//            addToStarred()
//            doubleTap = false
//        }
//        else {
//            doubleTap = true
//            var handler = Handler()
//            handler.postDelayed(r, 500)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()

        cacheDir.delete()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)
    }

    private fun chageSubreddit() {
        subreddit = edtSubreddit.text.toString()
        hideKeyboard(currentFocus ?: View(this@MainActivity))
        loadMeme()
    }

    fun Context.hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun loadMeme() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val imgMeme = findViewById<ImageView>(R.id.imgMeme)
        // Instantiate the RequestQueue.
        val url = "https://meme-api.com/gimme/$subreddit"
        Log.i("url", url)

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { responce ->
                currentImageUrl = responce.getString("url")
                imgList.add(0, currentImageUrl.toString())
                Log.e("list", imgList.toString())
                Glide.with(this).load(currentImageUrl).listener(object: RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgMeme)
                imgStar.setImageResource(R.drawable.ic_star_hollow)
                toggleStar = false

                if(starredItems.contains(currentImageUrl)) {
                    imgStar.setImageResource(R.drawable.ic_star_filled)

                }
            },
            { })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun shareMeme() {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Check this meme, lol $currentImageUrl")
        val chooser = Intent.createChooser(intent, "share using...")
        startActivity(chooser)
    }

    fun nextMeme() {
        loadMeme()
    }

    private fun addToStarred() {

        if(!toggleStar) {
            imgStar.setImageResource(R.drawable.ic_star_filled)
            starredItems.add(currentImageUrl.toString())
            toggleStar = true
            saveStarredArray(starredItems, keyStarred)
        }
        else {
            imgStar.setImageResource(R.drawable.ic_star_hollow)
            starredItems.remove(currentImageUrl)
            toggleStar = false
            saveStarredArray(starredItems, keyStarred)
        }
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

    private fun loadMemeList() {

        val imgListUrl = imgList.get(pointer)

        Glide
            .with(this)
            .load(imgListUrl)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imgMeme)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val handler = Handler()
                    handler.postDelayed({
                        v.setBackgroundResource(R.color.white) //set the color to black
                        Log.e("set", "background")
                    }, 2000)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
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

                if(valueX >= MIN_DISTANCE) {
                    Log.e("dir", "left")
                    if(imgList.size - 1 > pointer)
                        pointer++;
                    Log.e("pointer", pointer.toString())
                    loadMemeList()
                }
                else if(valueX <= -MIN_DISTANCE) {
                    Log.e("dir", "right")
                    if(pointer == 0)
                        loadMeme()
                    else {
                        if(pointer != 0)
                            pointer--;
                        loadMemeList()
                    }
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

    override fun onClick(view: View) {

    }
}
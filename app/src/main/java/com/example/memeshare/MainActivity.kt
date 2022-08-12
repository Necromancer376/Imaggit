package com.example.memeshare

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.sharememes.MySingleton
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    val keyStarred: String = "STARRED"
    var currentImageUrl: String? = null

    var subreddit = ""
    var starredItems = ArrayList<String>()
    var toggleStar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        btnChange.setOnClickListener { chageSubreddit() }
        btnSeeStarred.setOnClickListener {
            startActivity(Intent(this@MainActivity,  StarredImagesActivity::class.java))
        }
        imgStar.setOnClickListener { addToStarred() }
        edtSubreddit.setSelectAllOnFocus(true)

        starredItems = getStarredArray(keyStarred)
        loadMeme()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)
        val actionbar = supportActionBar
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_back_button_image)
        }
        toolbar_main_activity.setNavigationOnClickListener{ onBackPressed() }
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
        val url = "https://meme-api.herokuapp.com/gimme/$subreddit"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { responce ->
                currentImageUrl = responce.getString("url")
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
                }).into(imgMeme)
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

    fun shareMeme(view: android.view.View) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Check this meme, lol $currentImageUrl")
        val chooser = Intent.createChooser(intent, "share using...")
        startActivity(chooser)
    }

    fun nextMeme(view: android.view.View) {
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

    fun logStarred() {
        for(i in 0..starredItems.size-1) {
            Log.e(i.toString(), starredItems[i])
        }
    }
}
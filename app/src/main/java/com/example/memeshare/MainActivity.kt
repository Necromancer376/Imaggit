package com.example.memeshare

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    var currentImageUrl: String? = null

    var subreddit = ""
    var starredItems = ArrayList<String>()
    var toggleStar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChange.setOnClickListener { chageSubreddit() }
        imgStar.setOnClickListener { addToStarred() }
        edtSubreddit.setSelectAllOnFocus(true)

        loadMeme()
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

        starredItems.add(currentImageUrl.toString())
        if(!toggleStar) {
            imgStar.setImageResource(R.drawable.ic_star_filled)
            toggleStar = true
        }
        else {
            imgStar.setImageResource(R.drawable.ic_star_hollow)
            toggleStar = false
        }
    }
}
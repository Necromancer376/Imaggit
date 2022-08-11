package com.example.memeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_full_image.*
import kotlinx.android.synthetic.main.activity_starred_images.*

class FullImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)
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
}
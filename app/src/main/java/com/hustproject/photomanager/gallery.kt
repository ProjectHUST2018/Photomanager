package com.hustproject.photomanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_trash.*

class gallery : AppCompatActivity() {

    var displayState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        toolbarGallery.setTitle((applicationContext as data).tmp.getPhotoName())
        setSupportActionBar(toolbarGallery)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if((applicationContext as data).tmp.photoTime != "-1")
            toolbarGallery.setSubtitle((applicationContext as data).tmp.photoTimeStd)
        toolbarGallery.setNavigationOnClickListener( View.OnClickListener {
            view: View -> finish()
        })

        Glide.with(this).load((applicationContext as data).tmp.thisItem).apply((applicationContext as data).opt2).into(photoView)
        toolbarGallery.bringToFront()

        photoView.setOnClickListener(View.OnClickListener {
            view:View -> when(displayState) {
                0 ->{
                    toolbarGallery.visibility = View.GONE
                    displayState = 1
                }
            else ->{
                    toolbarGallery.visibility = View.VISIBLE
                    displayState = 0
                }
            }
        })
    }
}

package com.hustproject.photomanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_gallery.*

class gallery : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        Glide.with(this).load((applicationContext as data).tmp.thisItem).apply((applicationContext as data).opt2).into(Image)
    }
}

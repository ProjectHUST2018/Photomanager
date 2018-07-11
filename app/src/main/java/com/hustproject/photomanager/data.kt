package com.hustproject.photomanager

import android.app.Application
import com.bumptech.glide.request.RequestOptions

class data: Application() {
    lateinit var album:Scanner
    lateinit var tmp:Photo
    val opt = RequestOptions().centerCrop()

    fun init() {
        album = Scanner()
    }
}
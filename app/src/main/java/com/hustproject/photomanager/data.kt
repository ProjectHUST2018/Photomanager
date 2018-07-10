package com.hustproject.photomanager

import android.app.Application
import com.bumptech.glide.request.RequestOptions

class data: Application() {
    public lateinit var album:Scanner
    public lateinit var tmp:Photo
    public val opt = RequestOptions().centerCrop()
    public val opt2= RequestOptions().fitCenter()

    public fun init() {
        album = Scanner()
    }

}
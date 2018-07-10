package com.hustproject.photomanager

import android.app.Application

class data: Application() {
    public lateinit var album:Scanner
    fun init() {
        album = Scanner()
    }
}
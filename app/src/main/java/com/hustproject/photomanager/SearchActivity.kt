package com.hustproject.photomanager

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_trash.*
import kotlinx.android.synthetic.main.photo.view.*
import java.util.*

class SearchActivity : AppCompatActivity() {

    lateinit var key: String
    private var raw: MutableList<LinearLayout> = mutableListOf()

    private fun pushin(tp: LinearLayout) {
        var margin = View.inflate(this, R.layout.margin, null) as LinearLayout
        containerSearch.addView(margin)
        raw.add(margin)
        containerSearch.addView(tp)
        raw.add(tp)
    }

    private class cmp1 : Comparator<Photo> {
        override fun compare(a: Photo, b: Photo): Int {
            if (a.countSec < b.countSec) return 1
            if (a.countSec == b.countSec) return 0
            return -1
        }
    }

    private class cmp2 : Comparator<Photo> {
        override fun compare(a: Photo, b: Photo): Int {
            if (a.photoSize < b.photoSize) return 1
            if (a.photoSize == b.photoSize) return 0
            return -1
        }
    }

    private class cmp3 : Comparator<Photo> {
        override fun compare(a: Photo, b: Photo): Int {
            if (a.modifyTime  < b.modifyTime) return 1
            if (a.modifyTime == b.modifyTime) return 0
            return -1
        }
    }

    private fun display(secPhoto:List<Photo>,mode: Int) {
        var count = 0
        lateinit var tp: LinearLayout
        for (item in raw)
            containerSearch.removeView(item)
        raw.clear()

        when (mode) {
            1 -> Collections.sort(secPhoto, cmp1())
            2 -> Collections.sort(secPhoto, cmp2())
            3 -> Collections.sort(secPhoto, cmp3())
        }

        for (i in secPhoto.indices) {
            if ((i == 0 || secPhoto[i - 1].photoTimeStd != secPhoto[i].photoTimeStd) && mode == 1) {
                if (i != 0) pushin(tp)

                tp = View.inflate(this, R.layout.time_tag, null) as LinearLayout

                if (secPhoto[i].photoTime != "-1")
                    (tp.findViewById(R.id.timeTag) as TextView).setText(" " + secPhoto[i].photoTimeStd + " ")
                else (tp.findViewById(R.id.timeTag) as TextView).setText(" 其他 ")

                count = 0
            }

            if (count == 0 ) {
                if(i != 0 || mode == 1)pushin(tp)
                tp = View.inflate(this, R.layout.photo, null) as LinearLayout
            }

            count++
            var thisImage:ImageView = when (count) {
                1 -> tp.imageView1
                2 -> tp.imageView2
                3 -> tp.imageView3
                else -> tp.imageView4
            }

            Glide.with(this).load(secPhoto[i].thisItem).apply((applicationContext as data).opt).into(thisImage)
            thisImage.setOnClickListener {
                view:View -> show(secPhoto[i])
            }

            if (count == 4) count = 0
        }

        if (secPhoto.isNotEmpty()) pushin(tp)
        pushin(View.inflate(this, R.layout.empty, null) as LinearLayout)
    }

    private fun show(photo:Photo) {
        (applicationContext as data).tmp = photo
        var starter = Intent(this,Gallery::class.java)
        startActivity(starter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        key = intent.getStringExtra("key")
        toolbarSearch.setTitle(key+"的搜索结果")
        setSupportActionBar(toolbarSearch)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarSearch.setNavigationOnClickListener( View.OnClickListener {
            view:View -> finish()
        })
    }

    override fun onStart() {
        super.onStart()
        display((applicationContext as data).album.getAllPhoto(key),1);
    }
}

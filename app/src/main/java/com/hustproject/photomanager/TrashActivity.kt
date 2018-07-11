package com.hustproject.photomanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*

import kotlinx.android.synthetic.main.activity_trash.*
import kotlinx.android.synthetic.main.photo.view.*
import java.util.*

class TrashActivity : AppCompatActivity() {

    private var raw: MutableList<LinearLayout> = mutableListOf()

    private fun pushin(tp: LinearLayout) {
        var margin = View.inflate(this, R.layout.margin, null) as LinearLayout
        containerTrash.addView(margin)
        raw.add(margin)
        containerTrash.addView(tp)
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
            containerTrash.removeView(item)
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
            thisImage.setOnClickListener(View.OnClickListener {
                view:View -> show(secPhoto[i])
            })
            if (count == 4) count = 0
        }

        if (secPhoto.isNotEmpty()) pushin(tp)
        pushin(View.inflate(this, R.layout.empty, null) as LinearLayout)
    }

    private fun show(photo:Photo) {
        (applicationContext as data).tmp = photo
        var starter = Intent(this,gallery::class.java)
        startActivity(starter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        toolbarTrash.setTitle(R.string.trash)
        setSupportActionBar(toolbarTrash)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarTrash.setNavigationOnClickListener( View.OnClickListener {
            view:View -> finish()
        })
    }

    override fun onStart() {
        super.onStart()
        display((applicationContext as data).album.getAllPhoto(2),4)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0,1,0,R.string.recAll)
        menu.add(0,2,0,R.string.delAll)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            1 ->{
                var tmp = (applicationContext as data).album.getAllPhoto(2)
                for(item in tmp)
                    (applicationContext as data).album.recover(item)
                Toast.makeText(this, resources.getString(R.string.allrec), Toast.LENGTH_LONG).show()
            }
            2 ->{
                var tmp = (applicationContext as data).album.getAllPhoto(2)
                for(item in tmp)
                    (applicationContext as data).album.remove(item)
                Toast.makeText(this, resources.getString(R.string.allrem), Toast.LENGTH_LONG).show()
            }
        }
        finish()
        return super.onOptionsItemSelected(item)
    }
}

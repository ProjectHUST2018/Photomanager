package com.hustproject.photomanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*
import kotlin.Comparator


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val opt = RequestOptions().centerCrop()
    private lateinit var album: Scanner

    private var sortMode: Int = 1
    private lateinit var raw: MutableList<LinearLayout>
    private var lastLen = 0

    private fun pushin(tp: LinearLayout) {
        var margin = View.inflate(this, R.layout.margin, null) as LinearLayout
        container.addView(margin)
        raw.add(margin)
        container.addView(tp)
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

    private fun display(mode: Int) {
        var count = 0
        lateinit var tp: LinearLayout
        var secPhoto = album.getAllPhoto(1)
        if(mode == sortMode && lastLen == secPhoto.size)return
        lastLen = secPhoto.size

        for (item in raw)
            container.removeView(item)
        raw.clear()

        when (mode) {
            1 -> Arrays.sort(secPhoto, cmp1())
            2 -> Arrays.sort(secPhoto, cmp2())
            3 -> Arrays.sort(secPhoto, cmp3())
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

            count++;
            when (count) {
                1 -> Glide.with(this).load(secPhoto[i].thisItem).apply(opt).into(tp.findViewById(R.id.imageView1))
                2 -> Glide.with(this).load(secPhoto[i].thisItem).apply(opt).into(tp.findViewById(R.id.imageView2))
                3 -> Glide.with(this).load(secPhoto[i].thisItem).apply(opt).into(tp.findViewById(R.id.imageView3))
                4 -> Glide.with(this).load(secPhoto[i].thisItem).apply(opt).into(tp.findViewById(R.id.imageView4))
            }

            if (count == 4) count = 0
        }
        if (secPhoto.isNotEmpty()) pushin(tp)
        var margin = View.inflate(this, R.layout.empty, null) as LinearLayout
        container.addView(margin)
        raw.add(margin)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            this.requestPermissions(PERMISSIONS_STORAGE, 1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        raw = mutableListOf()

        sortMode = 1
        setSupportActionBar(toolbar)
        searchView.setVoiceSearch(false)

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        album = Scanner()
        album.TagLoad()
    }

    override fun onStart() {
        super.onStart()
        album.fileScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
        searchView.setSuggestions(album.getAllTag())
        display(sortMode)
    }

    override fun onStop() {
        super.onStop()
        album.TagSave()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu,menu)
        var item:MenuItem  = menu.findItem(R.id.search)
        searchView.setMenuItem(item)
        searchView.bringToFront()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.trashView -> {
                var starter = Intent(this,TrashActivity::class.java)
                startActivity(starter)
            }
            R.id.timeSort -> {
                if(sortMode != 1){
                    sortMode = 1
                    display(sortMode)
                }
            }
            R.id.sizeSort -> {
                if(sortMode != 2) {
                    sortMode = 2
                    display(sortMode)
                }
            }
            R.id.modifySort -> {
                if(sortMode != 3) {
                    sortMode = 3
                    display(sortMode)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            finish()
        else
        {
            album = Scanner()
            album.TagLoad()
            album.fileScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
            searchView.setSuggestions(album.getAllTag())
            display(sortMode);
        }
    }
}
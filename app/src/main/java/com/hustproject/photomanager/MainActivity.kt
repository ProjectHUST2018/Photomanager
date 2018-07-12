package com.hustproject.photomanager

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.photo.*
import kotlinx.android.synthetic.main.photo.view.*
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var sortMode: Int = 1
    private var raw: MutableList<LinearLayout> = mutableListOf()

    private val dirPath = "/data/data/com.hustproject.photomanager/files/"
    private val dir = File(dirPath)
    val fst = File(dirPath+"IfFirst")

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

    private fun display(secPhoto:List<Photo>,mode: Int) {
        var count = 0
        lateinit var tp: LinearLayout
        for (item in raw) {
            container.removeView(item)
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            this.requestPermissions(PERMISSIONS_STORAGE, 1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sortMode = 1
        setSupportActionBar(toolbar)
        searchView.setVoiceSearch(false)

        var starter = Intent(this,SearchActivity::class.java)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                starter.putExtra("key",query)
                startActivity(starter)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        (applicationContext as data).init()
        if(fst.exists())(applicationContext as data).album.TagLoad()
    }

    override fun onStart() {
        super.onStart()
        if(fst.exists())(applicationContext as data).album.fileScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
        if(fst.exists())searchView.setSuggestions((applicationContext as data).album.getAllTag())
        if(fst.exists())display((applicationContext as data).album.getAllPhoto(1),sortMode)
    }

    override fun onStop() {
        super.onStop()
        (applicationContext as data).album.TagSave()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
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
                    display((applicationContext as data).album.getAllPhoto(1),sortMode)
                }
            }
            R.id.sizeSort -> {
                if(sortMode != 2) {
                    sortMode = 2
                    display((applicationContext as data).album.getAllPhoto(1),sortMode)
                }
            }
            R.id.modifySort -> {
                if(sortMode != 3) {
                    sortMode = 3
                    display((applicationContext as data).album.getAllPhoto(1),sortMode)
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
            if(!dir.exists())dir.mkdir()
            if(fst.exists())return
            fst.createNewFile()

            (applicationContext as data).album.TagLoad()
            (applicationContext as data).album.fileScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
            searchView.setSuggestions((applicationContext as data).album.getAllTag())
            display((applicationContext as data).album.getAllPhoto(1),sortMode)
        }
    }
}
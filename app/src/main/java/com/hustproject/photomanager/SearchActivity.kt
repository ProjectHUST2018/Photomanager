package com.hustproject.photomanager

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_trash.*

class SearchActivity : AppCompatActivity() {

    lateinit var key: String

    private fun pushin(tp: LinearLayout) {
        var margin = View.inflate(this, R.layout.margin, null) as LinearLayout
        containerSearch.addView(margin)
        containerSearch.addView(tp)
    }

    private fun display(secPhoto: List<Photo>) {
        var tp: LinearLayout = View.inflate(this, R.layout.photo, null) as LinearLayout

        for (i in secPhoto.indices) {
            if(i % 4 == 0 && i != 0) {
                pushin(tp)
                tp = View.inflate(this, R.layout.photo, null) as LinearLayout
            }

            var thisImage: ImageView = when (i % 4) {
                0 -> tp.findViewById(R.id.imageView1)
                1 -> tp.findViewById(R.id.imageView2)
                2 -> tp.findViewById(R.id.imageView3)
                else -> tp.findViewById(R.id.imageView4)
            }

            Glide.with(this).load(secPhoto[i].thisItem).apply((applicationContext as data).opt).into(thisImage)
            registerForContextMenu(thisImage)
        }
        if (secPhoto.isNotEmpty()) pushin(tp)

        var margin = View.inflate(this, R.layout.empty, null) as LinearLayout
        containerSearch.addView(margin)
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
            view: View -> finish()
        })

        display((applicationContext as data).album.getAllPhoto(key));
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu!!, v, menuInfo)
        menu.add(0, 1, Menu.NONE, "编辑标签")
        menu.add(0, 2, Menu.NONE, "移入回收站")
    }

}

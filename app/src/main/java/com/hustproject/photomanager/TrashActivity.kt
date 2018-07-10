package com.hustproject.photomanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_trash.*
import java.util.*

class TrashActivity : AppCompatActivity() {

    private val opt = RequestOptions().centerCrop()
    private lateinit var raw :MutableList<LinearLayout>

    private fun pushin(tp: LinearLayout) {
        var margin = View.inflate(this, R.layout.margin, null) as LinearLayout
        containerTrash.addView(margin)
        raw.add(margin)
        containerTrash.addView(tp)
        raw.add(tp)
    }

    private fun display(secPhoto: List<Photo>) {
        var tp: LinearLayout = View.inflate(this, R.layout.photo, null) as LinearLayout

        for (item in raw)
            containerTrash.removeView(item)
        raw.clear()

        for (i in secPhoto.indices) {
            if(i % 4 == 0 && i != 0) {
                pushin(tp)
                View.inflate(this, R.layout.photo, null) as LinearLayout
            }
            var thisImage: ImageView = when (i % 4) {
                0 -> tp.findViewById(R.id.imageView1)
                1 -> tp.findViewById(R.id.imageView2)
                2 -> tp.findViewById(R.id.imageView3)
                else -> tp.findViewById(R.id.imageView4)
            }
            Glide.with(this).load(secPhoto[i].thisItem).apply(opt).into(thisImage)
        }
        if (secPhoto.isNotEmpty()) pushin(tp)

        var margin = View.inflate(this, R.layout.empty, null) as LinearLayout
        containerTrash.addView(margin)
        raw.add(margin)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        raw = mutableListOf()

        setSupportActionBar(toolbarTrash)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarTrash.setNavigationOnClickListener( View.OnClickListener {
            view:View -> finish()
        })

        display((applicationContext as data).album.delPhoto)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.trash,menu)
        return true
    }
}

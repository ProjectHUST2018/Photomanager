package com.hustproject.photomanager

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_trash.*

class gallery : AppCompatActivity() {

    var displayState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        toolbarGallery.setTitle((applicationContext as data).tmp.photoName)
        setSupportActionBar(toolbarGallery)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if((applicationContext as data).tmp.photoTime != "-1")
            toolbarGallery.setSubtitle((applicationContext as data).tmp.photoTimeStd)
        toolbarGallery.setNavigationOnClickListener( View.OnClickListener {
            view: View -> finish()
        })

        Glide.with(this).load((applicationContext as data).tmp.thisItem).apply((applicationContext as data).opt2).into(photoView)
        toolbarGallery.bringToFront()

        photoView.setOnClickListener(View.OnClickListener {
            view:View -> when(displayState) {
                0 ->{
                    toolbarGallery.visibility = View.GONE
                    bottom.visibility = View.GONE
                    displayState = 1
                }
            else ->{
                   toolbarGallery.visibility = View.VISIBLE
                    bottom.visibility = View.VISIBLE
                    displayState = 0
                }
            }
        })

        if((applicationContext as data).tmp.isDeleted == true){
            tag.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings_backup_restore_white_48dp))

            delete.setOnClickListener(View.OnClickListener { View->
                (applicationContext as data).album.remove((applicationContext as data).tmp)
                Toast.makeText(this, resources.getString(R.string.rem), Toast.LENGTH_LONG).show()
                finish()
            })

            tag.setOnClickListener(View.OnClickListener { View->
                (applicationContext as data).album.recover((applicationContext as data).tmp)
                Toast.makeText(this, resources.getString(R.string.rec), Toast.LENGTH_LONG).show()
                finish()
            })
        }
        else {
            delete.setOnClickListener(View.OnClickListener { View ->
                (applicationContext as data).album.delete((applicationContext as data).tmp)
                Toast.makeText(this, resources.getString(R.string.del), Toast.LENGTH_LONG).show()
                finish()
            })
        }
    }
}

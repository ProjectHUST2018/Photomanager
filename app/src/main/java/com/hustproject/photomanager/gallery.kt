package com.hustproject.photomanager

import android.content.DialogInterface
import android.graphics.Color.rgb
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hustproject.photomanager.R.layout.tag_display
import com.hustproject.photomanager.R.layout.tag_present
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.tag_new.view.*
import kotlinx.android.synthetic.main.tag_present.view.*

class Gallery : AppCompatActivity() {

    private var displayState = 0
    lateinit var view : LinearLayout
    lateinit var input : LinearLayout

    private fun loadTag(){
        view.removeAllViews()
        for(tag in (applicationContext as data).tmp.Tag){
            val tp = View.inflate(this,tag_present,null) as LinearLayout
            tp.tg.text = tag
            tp.tg.setTextColor(rgb(0,0,0))
            view.addView(tp)

            tp.del.setOnClickListener { _->
                view.removeView(tp)
                (applicationContext as data).album.deleteTag(tag)
                for(photo in (applicationContext as data).album.allPhoto){
                    if(photo.Path.equals((applicationContext as data).tmp.Path)){
                        photo.delTag(tag)
                        break
                    }
                }
            }
        }
    }

    private fun createTag(tag:String){
        if(tag.isEmpty())return
        (applicationContext as data).album.addTag(tag)
        for(photo in (applicationContext as data).album.allPhoto){
            if(photo.Path.equals((applicationContext as data).tmp.Path)){
                photo.addTag(tag)
                break
            }
        }
        loadTag()
        input.read.setText("")
        view.addView(input)
        input.read.requestFocus()
    }

    private fun showTag(){
        view = View.inflate(this,tag_display,null) as LinearLayout

        loadTag()

        input = View.inflate(this,R.layout.tag_new,null) as LinearLayout
        view.addView(input)

        input.add.setOnClickListener { _ ->
            createTag(input.read.text.toString())
        }

        AlertDialog.Builder(this)
                .setTitle("标签")
                .setView(view)
                .setPositiveButton("离开",DialogInterface.OnClickListener{dialog,i ->
                    dialog.dismiss()
                }).create().show()
    }

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

        var opt2 = RequestOptions().fitCenter().override((applicationContext as data).tmp.width,(applicationContext as data).tmp.height)
        Glide.with(this).load((applicationContext as data).tmp.thisItem).apply(opt2).into(photoView)
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
            tag.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings_backup_restore_white_36dp))

            delete.setOnClickListener{ View->
                AlertDialog.Builder(this)
                        .setTitle("抹除照片")
                        .setMessage("\n"+"你确定要永久删除这张照片吗？")
                        .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, i ->
                        })
                        .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, i ->
                            (applicationContext as data).album.remove((applicationContext as data).tmp)
                            Toast.makeText(this, resources.getString(R.string.rem), Toast.LENGTH_LONG).show()
                            finish()
                        })
                        .create().show()
            }

            tag.setOnClickListener{ View->
                (applicationContext as data).album.recover((applicationContext as data).tmp)
                Toast.makeText(this, resources.getString(R.string.rec), Toast.LENGTH_LONG).show()
                finish()
            }
        }
        else {
            delete.setOnClickListener{ View ->
                (applicationContext as data).album.delete((applicationContext as data).tmp)
                Toast.makeText(this, resources.getString(R.string.del), Toast.LENGTH_LONG).show()
                finish()
            }

            tag.setOnClickListener { View ->
                showTag()
            }
        }

        var info = ""
        if(!(applicationContext as data).tmp.photoTime.equals("-1"))
            info += "\n"+"拍摄时间"+"\n"+(applicationContext as data).tmp.photoTimeStd+"\n"

        info += "\n"+"文件大小"+"\n"+(applicationContext as data).tmp.getPhotoSize()+"\n"
        info += "\n"+"图片尺寸"+"\n"+(applicationContext as data).tmp.width+" x "+(applicationContext as data).tmp.height+"\n"

        inf.setOnClickListener{_->
            AlertDialog.Builder(this)
                    .setTitle("详情")
                    .setMessage(info)
                    .setPositiveButton("关闭",DialogInterface.OnClickListener{dialog, i ->
                    })
                    .create().show()
        }
    }
}

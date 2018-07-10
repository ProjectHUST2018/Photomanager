package com.hustproject.photomanager

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_trash.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var trans = intent
        toolbarSearch.setTitle(trans.getStringExtra("key")+"的搜索结果")
        setSupportActionBar(toolbarSearch)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarSearch.setNavigationOnClickListener( View.OnClickListener {
            view: View -> finish()
        })
    }

}

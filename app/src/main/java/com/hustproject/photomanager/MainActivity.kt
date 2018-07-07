package com.hustproject.photomanager

import android.content.ClipData
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomNavigationView
import android.support.v7.view.menu.MenuView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ConcurrentHashMap


class MainActivity : AppCompatActivity() {

    private lateinit var album:Scanner
    private lateinit var toolbar: android.support.v7.widget.Toolbar
    private lateinit var search: MaterialSearchView

    private fun setAsTimeSort() {

    }

    private fun setAsSizeSort() {

    }

    private fun setAsModifySort() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        search = findViewById(R.id.searchView)

        setSupportActionBar(toolbar);

        album = Scanner()
        searchView.setVoiceSearch(false)
        //album.Load()
    }

    override fun onStart() {
        super.onStart()
        album.fileScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
        //searchView.setSuggestions(album)
        Toast.makeText(getApplicationContext(),"Done.",Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu,menu)
        var item:MenuItem  = menu.findItem(R.id.search)
        searchView.setMenuItem(item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.trashView -> {
                var starter = Intent(this,TrashActivity::class.java)
                startActivity(starter)
            }
            R.id.timeSort -> {
                setAsTimeSort()
            }
            R.id.sizeSort -> {
                setAsSizeSort()
            }
            R.id.modifySort -> {
                setAsModifySort()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

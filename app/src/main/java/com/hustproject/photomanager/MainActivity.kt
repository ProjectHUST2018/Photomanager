package com.hustproject.photomanager

import android.content.ClipData
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.view.menu.MenuView
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var tab: BottomNavigationView
    private lateinit var title:TextView;

    private fun loadAllPic() {
        title.setText(R.string.allPic)
    }

    private fun loadPhoto() {
        title.setText(R.string.photo)
    }

    private fun loadTrash() {
        title.setText(R.string.trash)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = findViewById(R.id.titleView)
        tab = findViewById(R.id.BottomBar)

       val listener = BottomNavigationView.OnNavigationItemSelectedListener {item:MenuItem ->
           when(item.itemId) {
                R.id.allPicView -> {
                    loadAllPic()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.photoView -> {
                    loadPhoto()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.trashView -> {
                    loadTrash()
                    return@OnNavigationItemSelectedListener true
                }
               else -> return@OnNavigationItemSelectedListener false
           }
        }
        tab.setOnNavigationItemSelectedListener(listener)

        tab.selectedItemId = R.id.photoView

        // Example of a call to a native method
        //sample_text.text = stringFromJNI()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //external fun stringFromJNI(): String

    /*companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }*/
}

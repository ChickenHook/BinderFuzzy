package org.chickenhook.binderfuzzy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.chickenhook.binderfuzzy.reflectionbrowser.RecentsActivity
import org.chickenhook.binderfuzzy.reflectionbrowser.ReflectionBrowser


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_new?.setOnClickListener {
            val intent = Intent(this, ReflectionBrowser::class.java)
            startActivity(intent)
        }
        main_recent?.setOnClickListener {
            val intent = Intent(this, RecentsActivity::class.java)
            startActivity(intent)
        }
    }
}

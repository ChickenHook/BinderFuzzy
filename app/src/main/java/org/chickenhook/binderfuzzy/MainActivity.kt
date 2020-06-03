package org.chickenhook.binderfuzzy

import android.content.Intent
import android.net.Uri
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
        support?.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=8UH5MBVYM3J36"))
            startActivity(browserIntent)
        }
        github?.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ChickenHook/BinderFuzzy"))
            startActivity(browserIntent)
        }
    }
}

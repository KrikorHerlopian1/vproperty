package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.squareup.picasso.Picasso
import edu.newhaven.krikorherlopian.android.vproperty.LocaleHelper
import edu.newhaven.krikorherlopian.android.vproperty.R
import kotlinx.android.synthetic.main.image.*

class ShowImageActivity : AppCompatActivity() {
    var title: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.image)
            var bundle: Bundle? = intent.extras
            var url = bundle?.getString("url")
            var text = bundle?.getString("text")
            title = bundle?.getString("title")
            setupToolBar()
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                }
            }, 750)
            textItem.text = text
            textItem.startAnimation(AnimationUtils.loadAnimation(this, R.anim.move))
            ViewCompat.setTransitionName(image, "MySecondTransition")
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.placeholderdetail)
                .into(image)
        } catch (e: Exception) {
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = title
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }
}

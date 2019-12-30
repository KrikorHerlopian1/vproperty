package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import com.squareup.picasso.Picasso
import edu.newhaven.krikorherlopian.android.vproperty.R
import kotlinx.android.synthetic.main.image.*

class ShowImageActivity : CustomAppCompatActivity() {
    var title: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.image)
            var bundle: Bundle? = intent.extras
            var url = bundle?.getString("url")
            var text = bundle?.getString("text")
            title = bundle?.getString("title")
            setUpToolbar(toolbar, title!!)
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
            Log.d("Show Image Activity", "Exception")
        }
    }


}

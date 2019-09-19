package edu.newhaven.krikorherlopian.android.vproperty

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*

/*
    Splash screen, every one second am changing the image on screen. An animation at start of application showing
    different nice real estate images.Once last image reached,I call to start the login page.
 */

class SplashActivity : AppCompatActivity() {
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Glide.with(this).load(R.drawable.image1_).into(imageView)
        animateImages()
    }

    fun animateImages() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                when (count) {
                    0 -> Glide.with(this@SplashActivity).load(R.drawable.image2).into(imageView)
                    1 -> Glide.with(this@SplashActivity).load(R.drawable.image3).into(imageView)
                    2 -> goToLogin()
                }
                count++
                animateImages()
            }
        }, 1000)
    }

    fun goToLogin() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
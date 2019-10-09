package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import edu.newhaven.krikorherlopian.android.vproperty.R
import kotlinx.android.synthetic.main.activity_splash.*


/*
    Splash screen, every one second am changing the image on screen. An animation at start of application showing
    different nice real estate images.Once last image reached,I call to start the login page.
 */

class SplashActivity : AppCompatActivity() {
    var count = 1
    var fromNotification: Boolean? = false
    var sec = 750L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var bundle: Bundle? = intent.extras
        fromNotification = bundle?.getBoolean("fromNotification")
        System.out.println("fromNotification" + fromNotification)
        Glide.with(this).load(R.drawable.splash0).into(imageView)
        animateImages()
    }

    fun animateImages() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                when (count) {
                    1 -> Glide.with(this@SplashActivity).load(R.drawable.splash1).placeholder(
                        R.drawable.splash0
                    ).into(
                        imageView
                    )
                    2 -> Glide.with(this@SplashActivity).load(R.drawable.splash2).placeholder(
                        R.drawable.splash1
                    ).into(
                        imageView
                    )
                    3 -> Glide.with(this@SplashActivity).load(R.drawable.splash3).placeholder(
                        R.drawable.splash2
                    ).into(
                        imageView
                    )
                    4 -> Glide.with(this@SplashActivity).load(R.drawable.splash4).placeholder(
                        R.drawable.splash3
                    ).into(
                        imageView
                    )
                    5 -> Glide.with(this@SplashActivity).load(R.drawable.splash5).placeholder(
                        R.drawable.splash4
                    ).into(
                        imageView
                    )
                    6 -> Glide.with(this@SplashActivity).load(R.drawable.splash6).placeholder(
                        R.drawable.splash5
                    ).into(
                        imageView
                    )
                    7 -> Glide.with(this@SplashActivity).load(R.drawable.splash7).placeholder(
                        R.drawable.splash6
                    ).into(
                        imageView
                    )
                    8 -> Glide.with(this@SplashActivity).load(R.drawable.splash8).placeholder(
                        R.drawable.splash7
                    ).into(
                        imageView
                    )
                    9 -> Glide.with(this@SplashActivity).load(R.drawable.splash9).placeholder(
                        R.drawable.splash8
                    ).into(
                        imageView
                    )
                    10 -> Glide.with(this@SplashActivity).load(R.drawable.splash10).placeholder(
                        R.drawable.splash9
                    ).into(
                        imageView
                    )
                    11 -> lastImage()
                    12 -> goToLogin()
                }
                count++
                animateImages()
            }
        }, sec)
    }

    fun lastImage() {
        Glide.with(this@SplashActivity).load(R.drawable.splash11).placeholder(
            R.drawable.splash10
        )
            .into(
                imageView
            )
        sec = 200L
    }

    fun goToLogin() {


        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()


    }
}
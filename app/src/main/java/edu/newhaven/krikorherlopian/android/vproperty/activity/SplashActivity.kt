package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import edu.newhaven.krikorherlopian.android.vproperty.*
import kotlinx.android.synthetic.main.activity_splash.*


/*
    Splash screen, every one second am changing the image on screen. Once last image reached,I navigate to login page.
    If user has auto login option enabled, I auto log him directly to the application and skip the splash animation.
 */
class SplashActivity : CustomAppCompatActivity() {
    var count = 1
    private lateinit var auth: FirebaseAuth
    var sharedPref: SharedPreferences? = null
    var sec = 750L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )
        var auto = sharedPref?.getBoolean(PREF_AUTO, true)
        loggedInUser = auth.currentUser
        if (loggedInUser != null && auto!!) {
            sharedPref = getSharedPreferences(
                PREFS_FILENAME,
                PRIVATE_MODE
            )
            var drawer = sharedPref?.getString(PREF_DRAWER, "default").toString()
            var intent: Intent? = null
            if (drawer.equals("default")) {
                intent = Intent(this@SplashActivity, HomeMenuActivity::class.java)

            } else {
                intent = Intent(this@SplashActivity, CustomHomeMenuActivity::class.java)
            }
            intent.putExtra("email", loggedInUser?.email?.toString())
            intent.putExtra("displayName", loggedInUser?.displayName?.toString())
            intent.putExtra("phoneNumber", loggedInUser?.phoneNumber?.toString())
            intent.putExtra("photoUrl", loggedInUser?.photoUrl?.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            count = 15
            finish()
        }
        Glide.with(this).load(R.drawable.splash0).into(imageView)
        animateImages()
    }

    fun animateImages() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
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
                } catch (e: Exception) {
                    Log.d("Splash Activity", "Exception")
                }
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
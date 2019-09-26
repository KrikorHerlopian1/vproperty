package edu.newhaven.krikorherlopian.android.vproperty

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

/*
        This page contains the home menu , first page after login.
        It has multiple menu options in navigation drawer, every menu assosciated with fragment.
 */
class HomeMenuActivity : AppCompatActivity() {
    var photoUrl: String? = ""
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)

        val header = navView.getHeaderView(0)

        var bundle: Bundle? = intent.extras
        var photoUrl = bundle!!.getString("photoUrl")
        var displayName = bundle.getString("displayName")
        var email = bundle.getString("email")


        header.headerSubTitle.text = displayName
        header.headerTitle.text = email

        Glide.with(this@HomeMenuActivity).load(photoUrl)
            .placeholder(R.drawable.profileplaceholder).apply(RequestOptions.circleCropTransform())
            .into(
                header.imageView
            )

        val navController = findNavController(R.id.fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        //if  menu drawer is open close it on click of button, else go out of app.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer((Gravity.LEFT))
        } else {
            super.onBackPressed()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
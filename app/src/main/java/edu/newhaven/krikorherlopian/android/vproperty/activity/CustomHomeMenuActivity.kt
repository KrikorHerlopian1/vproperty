package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.setUpPermissions
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.custom_menu.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


/*
        This page contains the custom menu (arc) , first page after login.
        It has multiple menu options in navigation drawer, every menu assosciated with fragment.
 */
class CustomHomeMenuActivity : AppCompatActivity() {

    var photoUrl: String? = ""
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_menu)
        setSupportActionBar(toolbar)
        var bundle: Bundle? = intent.extras
        var photoUrl = bundle!!.getString("photoUrl")
        var displayName = bundle.getString("displayName")
        var email = bundle.getString("email")
        var page = bundle.getInt("page", 0)
        setUpPermissions(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.syncState()
        val header = navView.getHeaderView(0)
        header.headerSubTitle.text = displayName
        header.headerTitle.text = email

        //load user profile to menu
        Glide.with(this@CustomHomeMenuActivity).load(photoUrl)
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
        if (page == 1) {
            navView.menu.performIdentifierAction(R.id.nav_settings, 0)
        }
    }

    override fun onBackPressed() {
        //if  menu drawer is open close it on click of button, else go back in screen.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_property) {
            val intent = Intent(this@CustomHomeMenuActivity, AddPropertyStepperActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
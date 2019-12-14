package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.CroperinoFileUtil
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImageView
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.fragmentActivityCommunication
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.FragmentActivityCommunication
import edu.newhaven.krikorherlopian.android.vproperty.loggedInUser
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.custom_menu.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/*
        This page contains the custom menu (arc) , first page after login.
        It has multiple menu options in navigation drawer, every menu assosciated with fragment.
 */
class CustomHomeMenuActivity : CustomAppCompatActivity(), FragmentActivityCommunication {

    var photoUrl: String? = ""
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_menu)
        setSupportActionBar(toolbar)
        fragmentActivityCommunication = this
        var bundle: Bundle? = intent.extras
        var photoUrl = bundle!!.getString("photoUrl")
        var displayName = bundle.getString("displayName")
        var email = bundle.getString("email")
        var page = bundle.getInt("page", 0)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.syncState()
        updateProfile()

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

    private fun prepareCroperino() {
        //prepare camera, gallery and ask for storage permissions.
        CroperinoConfig(
            "IMG_" + System.currentTimeMillis() + ".jpg",
            "/VProperty/Pictures",
            "/sdcard/VProperty/Pictures"
        )
        CroperinoFileUtil.verifyStoragePermissions(this@CustomHomeMenuActivity)
        CroperinoFileUtil.setupDirectory(this@CustomHomeMenuActivity)

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

    override fun addProfileButtonClicked() {
        com.theartofdev.edmodo.cropper.CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(this)

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


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun startActivityDet(property: Property) {
        val i = Intent(
            this@CustomHomeMenuActivity,
            PropertyDetailsActivity::class.java
        )
        i.putExtra("argPojo", property)
        startActivity(i)
    }

    override fun startActivityDetWithTransition(property: Property, imageView: ImageView) {
        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@CustomHomeMenuActivity, imageView, "MyTransition"
            )
        val i = Intent(
            this@CustomHomeMenuActivity,
            PropertyDetailsActivity::class.java
        )
        i.putExtra("argPojo", property)
        startActivity(i, options.toBundle())
    }

    override fun updateProfile() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val header = navView.getHeaderView(0)
                header.headerSubTitle.text = loggedInUser?.displayName
                header.headerTitle.text = loggedInUser?.email
                //load user profile to menu
                Glide.with(this@CustomHomeMenuActivity).load(loggedInUser?.photoUrl)
                    .placeholder(R.drawable.profileplaceholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(
                        header.imageView
                    )
            }
        }, 1500)
    }

    var currentPhotoPath: String? = ""
    var photoFile: File? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "edu.newhaven.krikorherlopian.android.vproperty.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CroperinoConfig.REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = com.theartofdev.edmodo.cropper.CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    Picasso.get()
                        .load(result.uri)
                        .placeholder(R.drawable.profileplaceholder)
                        .error(R.drawable.profileplaceholder)
                        .fit()
                        .into(profile_image!!)

                }
            }
            CroperinoConfig.REQUEST_TAKE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, this@CustomHomeMenuActivity)
                    Croperino.runCropImage(
                        photoFile,
                        this@CustomHomeMenuActivity,
                        true,
                        1,
                        1,
                        R.color.gray,
                        R.color.gray_variant
                    )
                }
            CroperinoConfig.REQUEST_PICK_FILE ->
                if (resultCode == Activity.RESULT_OK) {
                    photoFile = null
                    CroperinoFileUtil.newGalleryFile(data, this@CustomHomeMenuActivity)
                    Croperino.runCropImage(
                        CroperinoFileUtil.getTempFile(),
                        this@CustomHomeMenuActivity,
                        true,
                        0,
                        0,
                        R.color.gray,
                        R.color.gray_variant
                    )
                }
            CroperinoConfig.REQUEST_CROP_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    //cropped image returned is set to the imageview on  register layout
                    var i: Uri?
                    if (photoFile != null)
                        i = Uri.fromFile(photoFile!!)
                    else
                        i = Uri.fromFile(CroperinoFileUtil.getTempFile())
                    Glide.with(this@CustomHomeMenuActivity).load(i)
                        .placeholder(R.drawable.profileplaceholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(
                            profile_image
                        )
                }
        }
    }


    var globalMenu: Menu? = null
    var showMenu: Boolean = true
    override fun hideShowMenuItems(show: Boolean) {
        if (globalMenu != null) {
            showMenu = show
            onPrepareOptionsMenu(globalMenu)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        globalMenu = menu
        menu?.clear()
        if (showMenu)
            menuInflater.inflate(R.menu.menu, menu)
        return true
    }

}
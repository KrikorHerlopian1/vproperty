package edu.newhaven.krikorherlopian.android.vproperty.activity


import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.CroperinoFileUtil
import com.schibstedspain.leku.*
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.font
import kotlinx.android.synthetic.main.add_property.*

class AddPropertyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_property)
        setupToolBar()
        setUpPermissions()
        setUpFonts()
        Glide.with(this@AddPropertyActivity).load(R.drawable.placeholderdetail)
            .placeholder(R.drawable.placeholderdetail)
            .into(
                picture
            )
        prepareCroperino()
        addPictureLayout.setOnClickListener {
            addPictureClicked()
        }
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                toolbar.visibility = View.GONE
            }
            if (scrollY < oldScrollY) {
                toolbar.visibility = View.VISIBLE
            }
        })
    }

    private fun setUpPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    private fun addPictureClicked() {
        Croperino.prepareChooser(
            this@AddPropertyActivity,
            "" + resources.getString(R.string.capture_photo),
            ContextCompat.getColor(this@AddPropertyActivity, android.R.color.background_dark)
        )
    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(assets, "" + font)
        houseNameInputLayout.typeface = tf
        houseName.typeface = tf
        addressName.typeface = tf
        zipCodeInput.typeface = tf
        longitudeInput.typeface = tf
        latitudeInput.typeface = tf
        descriptionLayout.typeface = tf
        addressInputLayout.typeface = tf
        longitudeLayout.typeface = tf
        latitudeLayout.typeface = tf
        zipCodeLayout.typeface = tf
        descriptionInputLayout.typeface = tf
        descriptionInputLayout.typeface = tf
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.add_property)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }

    private fun prepareCroperino() {
        //prepare camera, gallery and ask for storage permissions.
        CroperinoConfig(
            "IMG_" + System.currentTimeMillis() + ".jpg",
            "/VProperty/Pictures",
            "/sdcard/VProperty/Pictures"
        )
        CroperinoFileUtil.setupDirectory(this@AddPropertyActivity)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        //noinspection SimplifiableIfStatement
        if (id == R.id.open_location) {
            showLocationPicker()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.locationmenu, menu)
        return true
    }

    fun showLocationPicker() {
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withGeolocApiKey(resources.getString(R.string.map_key))
            .withDefaultLocaleSearchZone()
            .withSatelliteViewHidden()
            .withGooglePlacesEnabled()
            .withGoogleTimeZoneEnabled()
            .withUnnamedRoadHidden()
            .build(applicationContext)
        startActivityForResult(locationPickerIntent, RC_LCOATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CroperinoConfig.REQUEST_TAKE_PHOTO ->
                    if (resultCode == Activity.RESULT_OK) {
                        /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                        Croperino.runCropImage(
                            CroperinoFileUtil.getTempFile(),
                            this@AddPropertyActivity,
                            true,
                            1,
                            1,
                            R.color.gray,
                            R.color.gray_variant
                        )
                    }
                CroperinoConfig.REQUEST_PICK_FILE ->
                    if (resultCode == Activity.RESULT_OK) {
                        CroperinoFileUtil.newGalleryFile(data, this@AddPropertyActivity)
                        Croperino.runCropImage(
                            CroperinoFileUtil.getTempFile(),
                            this@AddPropertyActivity,
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
                        var i = Uri.fromFile(CroperinoFileUtil.getTempFile())
                        Glide.with(this@AddPropertyActivity).load(i)
                            .placeholder(R.drawable.placeholderdetail)
                            .into(
                                picture
                            )
                    }
                RC_LCOATION -> {
                    val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                    val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    val postalcode = data.getStringExtra(ZIPCODE)
                    latitudeInput.setText("" + latitude)
                    longitudeInput.setText("" + longitude)
                    addressName.setText(address.toString())
                    zipCodeInput.setText(postalcode.toString())
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val RC_LCOATION = 9003
    }
}
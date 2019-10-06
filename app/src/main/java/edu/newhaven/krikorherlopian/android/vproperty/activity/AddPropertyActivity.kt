package edu.newhaven.krikorherlopian.android.vproperty.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.schibstedspain.leku.*
import edu.newhaven.krikorherlopian.android.vproperty.R
import kotlinx.android.synthetic.main.activity_register.toolbar
import kotlinx.android.synthetic.main.add_property.*

class AddPropertyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_property)
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
            .shouldReturnOkOnBackPressed()
            .withStreetHidden()
            .withCityHidden()
            .withZipCodeHidden()
            .withSatelliteViewHidden()
            .withGooglePlacesEnabled()
            .withGoogleTimeZoneEnabled()
            .withUnnamedRoadHidden()
            .build(applicationContext)
        startActivityForResult(locationPickerIntent, RC_LCOATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RESULT****", "OK")
            if (requestCode == RC_LCOATION) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                val address = data.getStringExtra(LOCATION_ADDRESS)
                val postalcode = data.getStringExtra(ZIPCODE)
                latitudeInput.setText("" + latitude)
                longitudeInput.setText("" + longitude)
                addressName.setText(address.toString())
                zipCodeInput.setText(postalcode.toString())
            } else if (requestCode == 2) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                val address = data.getStringExtra(LOCATION_ADDRESS)
                Log.d("ADDRESS****", address.toString())
                val lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
                Log.d("LekuPoi****", lekuPoi.toString())
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val RC_LCOATION = 9003
    }
}
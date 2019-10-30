package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.property_details.*


class PropertyDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var prop: Property
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.property_details)
        prop = intent.getSerializableExtra("argPojo") as Property
        setupToolBar()
        Picasso.get()
            .load(prop.photoUrl)
            .placeholder(R.drawable.placeholderdetail)
            .into(image)
        address.text = prop.address.addressName
        var priceFormat = String.format(
            "%,.2f",
            prop.homeFacts.price?.toFloat()
        )

        if (prop.homeFacts.isRent)
            price.text = priceFormat + " $$"
        else
            price.text = priceFormat + " $$"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = prop.houseName
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.phone) {
            val mobNum = prop.contactPhone
            val phoneIntent = Intent(
                Intent.ACTION_DIAL, Uri.fromParts(
                    "tel", mobNum, null
                )
            )
            startActivity(phoneIntent)
        } else if (id == R.id.email) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:" + prop.email) // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, "" + prop.email)
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (prop.email != null && !prop.email!!.trim().equals("")) {
            if (prop.contactPhone != null && !prop.contactPhone.trim().equals("")) {
                menuInflater.inflate(R.menu.contact, menu)
            } else
                menuInflater.inflate(R.menu.email, menu)
        } else {
            if (prop.contactPhone != null && !prop.contactPhone.trim().equals("")) {
                menuInflater.inflate(R.menu.phone, menu)
            }
        }

        return true
    }

    override fun onMapReady(p0: GoogleMap?) {
        val sydney = LatLng(
            prop.address.latitude.toDouble(),
            prop.address.longitude.toDouble()
        )
        System.out.println(prop.address.latitude)
        System.out.println(prop.address.longitude)
        p0?.addMarker(MarkerOptions().position(sydney).title(""))
        p0?.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
    }
}
package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import edu.newhaven.krikorherlopian.android.vproperty.*
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ActivityFunctionalities
import edu.newhaven.krikorherlopian.android.vproperty.model.ItemValuePair
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.amenities.view.*
import kotlinx.android.synthetic.main.detail_item.view.hometype
import kotlinx.android.synthetic.main.detail_item.view.hometypevalue
import kotlinx.android.synthetic.main.detail_item.view.typeimage
import kotlinx.android.synthetic.main.detail_item_tint.view.*
import kotlinx.android.synthetic.main.property_details.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PropertyDetailsActivity : AppCompatActivity(), OnMapReadyCallback, ActivityFunctionalities {
    lateinit var prop: Property
    var imagePath = ""
    override fun closeActivity() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.property_details)
        activityFunctionalities = this
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

        if (loggedInUser?.email.equals(prop.email)) {
            fab.visibility = View.VISIBLE
            layout_for_fab.visibility = View.VISIBLE
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setUpSaleOrRent()
        setUpHomeFacts()
        setUpAddress()
        setUpRoomDetails()
        setUpBuildingDetails()
        setUpUtilityDetails()

        image.setOnClickListener {
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, imagelayout, "MySecondTransition"
                )
            val i = Intent(
                this@PropertyDetailsActivity,
                ShowImageActivity::class.java
            )
            i.putExtra("title", prop.houseName)
            i.putExtra("url", prop.photoUrl)
            i.putExtra("text", prop.address.addressName)
            startActivity(i, options.toBundle())
        }
        fab.setOnClickListener {
            val intent =
                Intent(this@PropertyDetailsActivity, AddPropertyStepperActivity::class.java)
            intent.putExtra("argPojo", prop)
            startActivity(intent)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
    private fun setUpUtilityDetails() {
        setUpHeatingType()
        setUpCoolingType()
    }

    private fun setUpHeatingType() {
        val list = prop.utilityDetails.heatingType.returnListAvailable(this)
        if (list.size > 0) {
            heatingtype.visibility = View.VISIBLE
            heatingtypelayout.visibility = View.VISIBLE
        } else {
            heatingtype.visibility = View.GONE
            heatingtypelayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.heating_type), list, heatingtypelayout)
    }

    private fun setUpCoolingType() {
        var list: MutableList<ItemValuePair> =
            prop.utilityDetails.coolingType.returnListAvailable(this)
        if (list.size > 0) {
            coolingtype.visibility = View.VISIBLE
            coolingtypelayout.visibility = View.VISIBLE
        } else {
            coolingtype.visibility = View.GONE
            coolingtypelayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.cooling_type), list, coolingtypelayout)
    }

    private fun setUpBuildingDetails() {
        setUpExterior()
        setUpBuildingAminities()
        setOutdoorAminities()
        setUpParking()
    }

    private fun setUpParking() {
        var list: MutableList<ItemValuePair> =
            prop.buildingDetails.parking.returnListAvailable(this)
        if (list.size > 0) {
            parking.visibility = View.VISIBLE
            parkinglayout.visibility = View.VISIBLE
        } else {
            parking.visibility = View.GONE
            parkinglayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.parking), list, parkinglayout)
    }


    private fun setOutdoorAminities() {
        var list: MutableList<ItemValuePair> =
            prop.buildingDetails.outdoorAminities.returnListAvailable(this)
        if (list.size > 0) {
            outdoor.visibility = View.VISIBLE
            outdoorlayout.visibility = View.VISIBLE
        } else {
            outdoor.visibility = View.GONE
            outdoorlayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.outdoor_aminities), list, outdoorlayout)
    }

    private fun setUpBuildingAminities() {
        var list: MutableList<ItemValuePair> =
            prop.buildingDetails.buildingAminities.returnListAvailable(this)
        if (list.size > 0) {
            buildingam.visibility = View.VISIBLE
            buildingamlayout.visibility = View.VISIBLE
        } else {
            buildingamlayout.visibility = View.GONE
            buildingam.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.building_aminities), list, buildingamlayout)
    }


    private fun setUpExterior() {
        var list: MutableList<ItemValuePair> =
            prop.buildingDetails.exterior.returnListAvailable(this)

        if (list.size > 0) {
            exterior.visibility = View.VISIBLE
            exteriorlayout.visibility = View.VISIBLE
        } else {
            exterior.visibility = View.GONE
            exteriorlayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.buidling_exterior), list, exteriorlayout)
    }

    private fun setUpRoomDetails() {
        setUpAppliances()
        setUpFloorCovering()
        setUpRooms()
    }

    private fun setUpRooms() {
        var list: MutableList<ItemValuePair> = prop.roomDetails.rooms.returnListAvailable(this)
        if (list.size > 0) {
            roomdetails.visibility = View.VISIBLE
            roomdetailslayout.visibility = View.VISIBLE
        } else {
            roomdetails.visibility = View.GONE
            roomdetailslayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.rooms), list, roomdetailslayout)
    }

    private fun setUpFloorCovering() {
        var list: MutableList<ItemValuePair> =
            prop.roomDetails.floorCovering.returnListAvailable(this)
        if (list.size > 0) {
            floorcoveringlayout.visibility = View.VISIBLE
            floorcovering.visibility = View.VISIBLE
        } else {
            floorcoveringlayout.visibility = View.GONE
            floorcovering.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.floor_covering), list, floorcoveringlayout)
    }

    private fun setUpAppliances() {
        var list: MutableList<ItemValuePair> = prop.roomDetails.appliances.returnListAvailable(this)
        if (list.size > 0) {
            applianceslayout.visibility = View.VISIBLE
            appliancesdetails.visibility = View.VISIBLE
        } else {
            applianceslayout.visibility = View.GONE
            appliancesdetails.visibility = View.GONE
        }

        setNewLayouts(resources.getString(R.string.appliances), list, applianceslayout)
    }

    private fun setNewLayouts(tex: String, list: MutableList<ItemValuePair>, layout: LinearLayout) {
        var child = layoutInflater.inflate(R.layout.amenities, null)
        var count = 0
        var last = 0
        for (itemvalue in list) {
            count = count + 1
            if (count % 2 != 0) {
                last = 1
                Glide.with(this@PropertyDetailsActivity).load(R.drawable.check).placeholder(
                    R.drawable.check
                ).into(
                    child.image1
                )
                child.text1.text = itemvalue.item1
            } else {
                last = 2
                Glide.with(this@PropertyDetailsActivity).load(R.drawable.check).placeholder(
                    R.drawable.check
                ).into(
                    child.image2
                )
                child.text2.text = itemvalue.item1
                layout.addView(child)
                child = layoutInflater.inflate(R.layout.amenities, null)
            }
        }
        if (last == 1) {
            layout.addView(child)
        }
    }

    private fun setUpAddress() {
        setUpImage()
        if (prop.address.descriptionAddress != null && !prop.address.descriptionAddress.equals("")
            && !prop.address.descriptionAddress.equals("0")
        ) {
            description.text = prop.address.descriptionAddress
        } else
            description.visibility = View.GONE
    }

    private fun setUpSaleOrRent() {
        if (prop.homeFacts.isSale) {
            Glide.with(this@PropertyDetailsActivity).load(R.drawable.ic_for_sale).placeholder(
                R.drawable.ic_for_sale
            ).into(
                imagetype
            )
        } else {
            Glide.with(this@PropertyDetailsActivity).load(R.drawable.ic_for_rent_color).placeholder(
                R.drawable.ic_for_rent_color
            ).into(
                imagetype
            )
        }
    }

    private fun setUpHomeFacts() {
        setUpHomeType()
        var list: MutableList<ItemValuePair> = prop.homeFacts.returnListAvailable(this)
        for (itemvalue in list) {
            val child = layoutInflater.inflate(R.layout.detail_item_tint, null)
            Glide.with(this@PropertyDetailsActivity).load(itemvalue.resId!!).placeholder(
                itemvalue.resId!!
            ).into(
                child.typeimage
            )
            child.hometype.text = itemvalue.item1
            child.hometypevalue.text = itemvalue.item2
            homeFactLayout.addView(child)
        }

        if (prop.relatedWebsite != null && !prop.relatedWebsite.equals("")
            && !prop.relatedWebsite.equals("0")
        ) {
            val child = layoutInflater.inflate(R.layout.detail_item_tint, null)
            Glide.with(this@PropertyDetailsActivity).load(R.drawable.ic_web_black_24dp).placeholder(
                R.drawable.ic_web_black_24dp
            ).into(
                child.typeimage
            )
            child.secondlayout.visibility = View.GONE
            //  child.hometype.text = resources.getString(R.string.related_website)
            child.hometype.text = Html.fromHtml("<u>" + prop.relatedWebsite + "</u>")
            homeFactLayout.addView(child)
            child.firstlayout.setOnClickListener {
                try {
                    val uris = Uri.parse(prop.relatedWebsite)
                    val intents = Intent(Intent.ACTION_VIEW, uris)
                    val b = Bundle()
                    b.putBoolean("new_window", true)
                    intents.putExtras(b)
                    startActivity(intents)
                } catch (e: Exception) {
                }

            }
        }

        if (prop.virtualTour != null && !prop.virtualTour.equals("")
            && !prop.virtualTour.equals("0")
        ) {
            val child = layoutInflater.inflate(R.layout.detail_item_tint, null)
            Glide.with(this@PropertyDetailsActivity).load(R.drawable.ic_web_black_24dp).placeholder(
                R.drawable.ic_web_black_24dp
            ).into(
                child.typeimage
            )
            child.secondlayout.visibility = View.GONE
            //  child.hometype.text = resources.getString(R.string.related_website)
            child.hometype.text =
                Html.fromHtml("<u>" + resources.getString(R.string.virtual_tour) + "</u>")
            homeFactLayout.addView(child)
            child.firstlayout.setOnClickListener {
                try {
                    val uris = Uri.parse(prop.virtualTour)
                    val intents = Intent(Intent.ACTION_VIEW, uris)
                    val b = Bundle()
                    b.putBoolean("new_window", true)
                    intents.putExtras(b)
                    startActivity(intents)
                } catch (e: Exception) {
                }

            }
        }


    }

    private fun setUpHomeType() {
        val child = layoutInflater.inflate(R.layout.detail_item, null)
        Glide.with(this@PropertyDetailsActivity).load(R.drawable.type).placeholder(
            R.drawable.type
        ).into(
            child.typeimage
        )

        child.hometype.text = resources.getString(R.string.type)
        child.hometypevalue.text = returnType()
        homeFactLayout.addView(child)
    }

    fun returnType(): String {
        if (prop.homeFacts.homeType.equals("SIF"))
            return resources.getString(R.string.single_family)
        else if (prop.homeFacts.homeType.equals("CON"))
            return resources.getString(R.string.condo)
        else if (prop.homeFacts.homeType.equals("TOW"))
            return resources.getString(R.string.town_house)
        else if (prop.homeFacts.homeType.equals("MUF"))
            return resources.getString(R.string.multi_family)
        else if (prop.homeFacts.homeType.equals("APT"))
            return resources.getString(R.string.apartment)
        else if (prop.homeFacts.homeType.equals("MOB"))
            return resources.getString(R.string.mobile_manufactured)
        else if (prop.homeFacts.homeType.equals("COU"))
            return resources.getString(R.string.coop_unit)
        else if (prop.homeFacts.homeType.equals("VAL"))
            return resources.getString(R.string.vacant_land)
        else
            return resources.getString(R.string.other)
    }

    private fun setUpImage() {
        Glide.with(this@PropertyDetailsActivity).load(prop.photoUrl).placeholder(
            R.drawable.placeholderdetail
        ).into(
            image
        )
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
        } else if (id == R.id.share) {
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "image/*"
            var bm = takeScreenshot()
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Contact " + prop.email)
            sharingIntent.putExtra(Intent.EXTRA_STREAM, saveImageExternal(bm))
            startActivities(arrayOf(Intent.createChooser(sharingIntent, "Share with")))
        }
        return super.onOptionsItemSelected(item)
    }

    fun takeScreenshot(): Bitmap {
        imagelayout!!.isDrawingCacheEnabled = true
        return imagelayout!!.drawingCache!!
    }

    private fun saveImageExternal(image: Bitmap): Uri? {
        //TODO - Should be processed in another thread
        var uri: Uri? = null
        try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to-share.jpeg")

            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.close()
            uri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                file
            )//Uri.fromFile(file)
        } catch (e: IOException) {
        }
        return uri
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
        val latLng = LatLng(
            prop.address.latitude.toDouble(),
            prop.address.longitude.toDouble()
        )
        p0?.addMarker(
            MarkerOptions().position(latLng).title("").icon(getMarkerIcon())
        )
        p0?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
    }
}
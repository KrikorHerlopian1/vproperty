package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.ItemValuePair
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.detail_item.view.*
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
        setUpSaleOrRent()
        setUpHomeFacts()
        setUpAddress()
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
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.homeFacts.bedrooms != null && !prop.homeFacts.bedrooms.equals("")
            && !prop.homeFacts.bedrooms.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.number_of_bedrooms), prop.homeFacts.bedrooms,
                    R.drawable.ic_bed
                )
            )
        }
        if (prop.homeFacts.bathrooms != null && !prop.homeFacts.bathrooms.equals("")
            && !prop.homeFacts.bathrooms.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.number_of_bathrooms), prop.homeFacts.bathrooms,
                    R.drawable.ic_toilet
                )
            )
        }
        if (prop.homeFacts.totalRooms != null && !prop.homeFacts.totalRooms.equals("")
            && !prop.homeFacts.totalRooms.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.total_rooms), prop.homeFacts.totalRooms,
                    R.drawable.ic_living_room
                )
            )
        }
        if (prop.homeFacts.yearBuilt != null && !prop.homeFacts.yearBuilt.equals("")
            && !prop.homeFacts.yearBuilt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.year_built), prop.homeFacts.yearBuilt,
                    R.drawable.ic_2019
                )
            )
        }
        if (prop.homeFacts.hoadues != null && !prop.homeFacts.hoadues.equals("")
            && !prop.homeFacts.hoadues.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.hoa_dues), prop.homeFacts.hoadues,
                    R.drawable.ic_dollar_symbol
                )
            )
        }
        if (prop.homeFacts.structuralModalYear != null && !prop.homeFacts.structuralModalYear.equals(
                ""
            )
            && !prop.homeFacts.structuralModalYear.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.modal_year), prop.homeFacts.structuralModalYear,
                    R.drawable.ic_2019
                )
            )
        }
        if (prop.homeFacts.floorNumber != null && !prop.homeFacts.floorNumber.equals("")
            && !prop.homeFacts.floorNumber.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.floor_number), prop.homeFacts.floorNumber,
                    R.drawable.ic_format_list_numbered_black_24dp
                )
            )
        }
        if (prop.homeFacts.finishedSqFt != null && !prop.homeFacts.finishedSqFt.equals("")
            && !prop.homeFacts.finishedSqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.finished_square_feet), prop.homeFacts.finishedSqFt,
                    R.drawable.ic_full_size
                )
            )
        }
        if (prop.homeFacts.lotSizeFqFt != null && !prop.homeFacts.lotSizeFqFt.equals("")
            && !prop.homeFacts.lotSizeFqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.lot_size), prop.homeFacts.lotSizeFqFt,
                    R.drawable.ic_full_size
                )
            )
        }
        if (prop.homeFacts.basementSqFt != null && !prop.homeFacts.basementSqFt.equals("")
            && !prop.homeFacts.basementSqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.basement_sq_ft), prop.homeFacts.basementSqFt,
                    R.drawable.ic_full_size
                )
            )
        }
        if (prop.homeFacts.garageSqFt != null && !prop.homeFacts.garageSqFt.equals("")
            && !prop.homeFacts.garageSqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    resources.getString(R.string.garage_ft), prop.homeFacts.garageSqFt,
                    R.drawable.ic_full_size
                )
            )
        }

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

    private fun returnType(): String {
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
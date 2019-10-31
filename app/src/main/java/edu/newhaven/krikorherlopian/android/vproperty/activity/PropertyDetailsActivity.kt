package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
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
import edu.newhaven.krikorherlopian.android.vproperty.activityFunctionalities
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ActivityFunctionalities
import edu.newhaven.krikorherlopian.android.vproperty.loggedInUser
import edu.newhaven.krikorherlopian.android.vproperty.model.ItemValuePair
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.amenities.view.*
import kotlinx.android.synthetic.main.detail_item.view.hometype
import kotlinx.android.synthetic.main.detail_item.view.hometypevalue
import kotlinx.android.synthetic.main.detail_item.view.typeimage
import kotlinx.android.synthetic.main.detail_item_tint.view.*
import kotlinx.android.synthetic.main.property_details.*


class PropertyDetailsActivity : AppCompatActivity(), OnMapReadyCallback, ActivityFunctionalities {
    lateinit var prop: Property
    var roomVisible: Boolean = false
    var buildingVisible: Boolean = false
    var utilityVisible: Boolean = false

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

        fab.setOnClickListener {
            val intent =
                Intent(this@PropertyDetailsActivity, AddPropertyStepperActivity::class.java)
            intent.putExtra("argPojo", prop)
            startActivity(intent)
        }
    }

    private fun setUpUtilityDetails() {
        setUpHeatingType()
        setUpCoolingType()
    }

    private fun setUpHeatingType() {
        utilityVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.utilityDetails.heatingType.baseboard) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.baseboard), "", 0))
        }
        if (prop.utilityDetails.heatingType.forcedAir) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.forced_air), "", 0))
        }
        if (prop.utilityDetails.heatingType.geoThermal) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.geothermal), "", 0))
        }
        if (prop.utilityDetails.heatingType.heatPump) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.heat_pump), "", 0))
        }
        if (prop.utilityDetails.heatingType.radiant) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.radiant), "", 0))
        }
        if (prop.utilityDetails.heatingType.stove) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.stove), "", 0))
        }
        if (prop.utilityDetails.heatingType.wall) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.wall), "", 0))
        }
        if (prop.utilityDetails.heatingType.other) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.other), "", 0))
        }
        if (utilityVisible) {
            heatingtype.visibility = View.VISIBLE
            heatingtypelayout.visibility = View.VISIBLE
        } else {
            heatingtype.visibility = View.GONE
            heatingtypelayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.heating_type), list, heatingtypelayout)
    }

    private fun setUpCoolingType() {
        utilityVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.utilityDetails.coolingType.central) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.central), "", 0))
        }
        if (prop.utilityDetails.coolingType.evaporative) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.evaporative), "", 0))
        }
        if (prop.utilityDetails.coolingType.geoThermal) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.geothermal), "", 0))
        }
        if (prop.utilityDetails.coolingType.refrigeration) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.refrigeration), "", 0))
        }
        if (prop.utilityDetails.coolingType.solar) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.solar), "", 0))
        }
        if (prop.utilityDetails.coolingType.wall) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.wall), "", 0))
        }
        if (prop.utilityDetails.coolingType.none) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.none), "", 0))
        }
        if (prop.utilityDetails.coolingType.other) {
            utilityVisible = true
            list.add(ItemValuePair(resources.getString(R.string.other), "", 0))
        }
        if (utilityVisible) {
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
        buildingVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.buildingDetails.parking.carport) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.carport), "", 0))
        }
        if (prop.buildingDetails.parking.garageAttached) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.garage_attached), "", 0))
        }
        if (prop.buildingDetails.parking.garageDetached) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.garage_detached), "", 0))
        }
        if (prop.buildingDetails.parking.offStreet) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.off_street), "", 0))
        }
        if (prop.buildingDetails.parking.onStreet) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.on_street), "", 0))
        }
        if (prop.buildingDetails.parking.none) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.none), "", 0))
        }

        if (buildingVisible) {
            parking.visibility = View.VISIBLE
            parkinglayout.visibility = View.VISIBLE
        } else {
            parking.visibility = View.GONE
            parkinglayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.parking), list, parkinglayout)
    }


    private fun setOutdoorAminities() {
        buildingVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.buildingDetails.outdoorAminities.balconyPatio) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.balcony_patio), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.barbecueArea) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.barbecue_area), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.deck) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.deck), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.dock) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.dock), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.fencedYard) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.fenced_yard), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.garden) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.garden), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.greenHouse) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.greenhouse), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.hotTubSpa) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.hottubspa), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.lawn) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.lawn), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.pond) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.pond), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.pool) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.pool), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.porch) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.porch), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.rvParking) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.rv_parking), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.sauna) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.sauna), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.sprinkerlSystem) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.sprinkler_system), "", 0))
        }
        if (prop.buildingDetails.outdoorAminities.waterfront) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.waterfront), "", 0))
        }
        if (buildingVisible) {
            outdoor.visibility = View.VISIBLE
            outdoorlayout.visibility = View.VISIBLE
        } else {
            outdoor.visibility = View.GONE
            outdoorlayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.outdoor_aminities), list, outdoorlayout)
    }

    private fun setUpBuildingAminities() {
        buildingVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.buildingDetails.buildingAminities.basketballCourt) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.basketball_court), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.disabledAccess) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.disabled_access), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.doorman) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.doorman), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.elevator) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.elevator), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.fitnessCenter) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.fitness_center), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.nearTransportation) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.near_transportation), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.sportsCourt) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.sports_court), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.storage) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.storage), "", 0))
        }
        if (prop.buildingDetails.buildingAminities.tennisCourt) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.tennis_court), "", 0))
        }
        if (buildingVisible) {
            buildingam.visibility = View.VISIBLE
            buildingamlayout.visibility = View.VISIBLE
        } else {
            buildingamlayout.visibility = View.GONE
            buildingam.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.building_aminities), list, buildingamlayout)
    }


    private fun setUpExterior() {
        buildingVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.buildingDetails.exterior.brick) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.brick), "", 0))
        }
        if (prop.buildingDetails.exterior.cement) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.cement), "", 0))
        }
        if (prop.buildingDetails.exterior.composition) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.composition), "", 0))
        }
        if (prop.buildingDetails.exterior.metal) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.metal), "", 0))
        }
        if (prop.buildingDetails.exterior.shingle) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.shingle), "", 0))
        }
        if (prop.buildingDetails.exterior.stone) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.stone), "", 0))
        }
        if (prop.buildingDetails.exterior.stucco) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.stucco), "", 0))
        }
        if (prop.buildingDetails.exterior.vinyl) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.vinyl), "", 0))
        }
        if (prop.buildingDetails.exterior.wood) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.wood), "", 0))
        }
        if (prop.buildingDetails.exterior.woodProducts) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.wood_products), "", 0))
        }
        if (prop.buildingDetails.exterior.other) {
            buildingVisible = true
            list.add(ItemValuePair(resources.getString(R.string.other), "", 0))
        }
        if (buildingVisible) {
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
        roomVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.roomDetails.rooms.breakfast) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.breakfast), "", 0))
        }
        if (prop.roomDetails.rooms.dinning) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.dinning), "", 0))
        }
        if (prop.roomDetails.rooms.family) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.family), "", 0))
        }
        if (prop.roomDetails.rooms.laundry) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.laundry), "", 0))
        }
        if (prop.roomDetails.rooms.library) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.library), "", 0))
        }
        if (prop.roomDetails.rooms.masterBath) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.master_bath), "", 0))
        }
        if (prop.roomDetails.rooms.mud) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.mud), "", 0))
        }
        if (prop.roomDetails.rooms.office) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.office), "", 0))
        }
        if (prop.roomDetails.rooms.pantry) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.pantry), "", 0))
        }
        if (prop.roomDetails.rooms.recreation) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.recreation), "", 0))
        }
        if (prop.roomDetails.rooms.solarium) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.solarium), "", 0))
        }
        if (prop.roomDetails.rooms.sun) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.sun), "", 0))
        }
        if (prop.roomDetails.rooms.walkInCloset) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.walk_in_closet), "", 0))
        }
        if (prop.roomDetails.rooms.workshop) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.workshop), "", 0))
        }
        if (roomVisible) {
            roomdetails.visibility = View.VISIBLE
            roomdetailslayout.visibility = View.VISIBLE
        } else {
            roomdetails.visibility = View.GONE
            roomdetailslayout.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.rooms), list, roomdetailslayout)
    }

    private fun setUpFloorCovering() {
        roomVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.roomDetails.floorCovering.carpet) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.carpet), "", 0))
        }
        if (prop.roomDetails.floorCovering.concrete) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.concrete), "", 0))
        }
        if (prop.roomDetails.floorCovering.hardwood) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.hardwood), "", 0))
        }
        if (prop.roomDetails.floorCovering.laminate) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.laminate), "", 0))
        }
        if (prop.roomDetails.floorCovering.linoleum) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.linoleum), "", 0))
        }
        if (prop.roomDetails.floorCovering.slate) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.slate), "", 0))
        }
        if (prop.roomDetails.floorCovering.softwood) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.softwood), "", 0))
        }
        if (prop.roomDetails.floorCovering.tile) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.tile), "", 0))
        }
        if (prop.roomDetails.floorCovering.other) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.other), "", 0))
        }
        if (roomVisible) {
            floorcoveringlayout.visibility = View.VISIBLE
            floorcovering.visibility = View.VISIBLE
        } else {
            floorcoveringlayout.visibility = View.GONE
            floorcovering.visibility = View.GONE
        }
        setNewLayouts(resources.getString(R.string.floor_covering), list, floorcoveringlayout)
    }

    private fun setUpAppliances() {
        roomVisible = false
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (prop.roomDetails.appliances.dishWasher) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.dishwasher), "", 0))
        }
        if (prop.roomDetails.appliances.washer) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.washer), "", 0))
        }
        if (prop.roomDetails.appliances.disposal) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.garbage_disposal), "", 0))
        }
        if (prop.roomDetails.appliances.dryer) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.dryer), "", 0))
        }
        if (prop.roomDetails.appliances.freezer) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.freezer), "", 0))
        }
        if (prop.roomDetails.appliances.refrigerator) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.refrigerator), "", 0))
        }
        if (prop.roomDetails.appliances.microwave) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.microwave), "", 0))
        }
        if (prop.roomDetails.appliances.rangeoven) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.range_oven), "", 0))
        }
        if (prop.roomDetails.appliances.trashCompactor) {
            roomVisible = true
            list.add(ItemValuePair(resources.getString(R.string.trash_compactor), "", 0))
        }

        if (roomVisible) {
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
        p0?.addMarker(MarkerOptions().position(sydney).title(""))
        p0?.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
    }
}
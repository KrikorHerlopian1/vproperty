package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.activity.SearchActivity
import edu.newhaven.krikorherlopian.android.vproperty.adapter.RecylerViewAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.*
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_filter.view.*
import kotlinx.android.synthetic.main.appliances.view.*
import kotlinx.android.synthetic.main.building_amenities.view.*
import kotlinx.android.synthetic.main.cooling_type.view.*
import kotlinx.android.synthetic.main.exterior.view.*
import kotlinx.android.synthetic.main.filter.view.*
import kotlinx.android.synthetic.main.floor_covering.view.*
import kotlinx.android.synthetic.main.floor_covering.view.other
import kotlinx.android.synthetic.main.heating_type.view.*
import kotlinx.android.synthetic.main.outdoor_amenities.view.*
import kotlinx.android.synthetic.main.parking.view.*
import kotlinx.android.synthetic.main.parking.view.none
import kotlinx.android.synthetic.main.room_details.view.*


class FilterFragment : Fragment(), ListClick {
    var root: View? = null
    var posSelected = -1
    var property: Property = Property()
    var typeCode: String? = ""
    var list: MutableList<Any> = mutableListOf<Any>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.filter, container, false)
        // home type. price range. sale or rent.
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                var child = layoutInflater.inflate(R.layout.activity_filter, null)
                root?.curveLoader?.visibility = View.GONE
                root!!.rootV!!.addView(child)
                setUpExpandableLayouts()
                setUpHomeTypes()
                root?.searchlayout?.setOnClickListener {
                    startSearch()
                }
                root?.forsale?.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        root?.forrent?.isChecked = false
                    }
                }


                root?.forrent?.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        root?.forsale?.isChecked = false
                    }
                }
            }
        }, 1000)

        return root
    }

    fun startSearch() {
        var appliances: Appliances = Appliances(
            root?.dishwasher!!.isChecked, root?.dryer!!.isChecked,
            root?.freezer!!.isChecked, root?.garbage_disposal!!.isChecked,
            root?.microwave!!.isChecked, root?.range_oven!!.isChecked,
            root?.refrigerator!!.isChecked, root?.washer!!.isChecked,
            root?.trash_compactor!!.isChecked
        )
        var floorCovering: FloorCovering = FloorCovering(
            root?.carpet!!.isChecked, root?.concrete!!.isChecked,
            root?.hardwood!!.isChecked, root?.laminate!!.isChecked,
            root?.slate!!.isChecked, root?.softwood!!.isChecked,
            root?.linoleum!!.isChecked, root?.tile!!.isChecked,
            root?.floor_covering_layout?.other!!.isChecked
        )
        var room: Room = Room(
            root?.breakfast!!.isChecked, root?.dinning!!.isChecked,
            root?.family!!.isChecked, root?.laundry!!.isChecked,
            root?.library!!.isChecked, root?.master_bath!!.isChecked,
            root?.mud!!.isChecked, root?.office!!.isChecked,
            root?.pantry!!.isChecked, root?.recreation!!.isChecked,
            root?.workshop!!.isChecked, root?.solarium!!.isChecked,
            root?.sun!!.isChecked, root?.walk_in_closet!!.isChecked
        )
        var exterior: Exterior = Exterior(
            root?.brick!!.isChecked, root?.cement!!.isChecked,
            root?.composition!!.isChecked, root?.metal!!.isChecked,
            root?.shingle!!.isChecked, root?.stone!!.isChecked,
            root?.stucco!!.isChecked, root?.vinyl!!.isChecked,
            root?.wood!!.isChecked, root?.wood_products!!.isChecked,
            root?.exterior?.other!!.isChecked
        )

        var buildingAminities = BuildingAminities(
            root?.basketball_court!!.isChecked,
            root?.disabled_access!!.isChecked,
            root?.doorman!!.isChecked,
            root?.elevator!!.isChecked,
            root?.fitness_center!!.isChecked,
            root?.sports_court!!.isChecked,
            root?.storage!!.isChecked,
            root?.tennis_court!!.isChecked,
            root?.near_transportation!!.isChecked
        )

        var outdoorAminities = OutdoorAminities(
            root?.balcony_patio!!.isChecked, root?.lawn!!.isChecked,
            root?.barbecue_area!!.isChecked, root?.pond!!.isChecked,
            root?.deck!!.isChecked, root?.pool!!.isChecked,
            root?.dock!!.isChecked, root?.porch!!.isChecked,
            root?.fenced_yard!!.isChecked, root?.rv_parking!!.isChecked,
            root?.garden!!.isChecked, root?.sauna!!.isChecked,
            root?.greenhouse!!.isChecked, root?.sprinkler_system!!.isChecked,
            root?.hottubspa!!.isChecked, root?.waterfront!!.isChecked
        )

        var parking: Parking = Parking(
            root?.carport!!.isChecked, root?.on_street!!.isChecked,
            root?.off_street!!.isChecked, root?.garage_attached!!.isChecked,
            root?.garage_detached!!.isChecked, root?.parking?.none!!.isChecked
        )
        var coolingType: CoolingType = CoolingType(
            root?.central!!.isChecked, root?.evaporative!!.isChecked,
            root?.geothermal_cooling!!.isChecked, root?.refrigeration!!.isChecked,
            root?.solar!!.isChecked, root?.wall_cooling!!.isChecked,
            root?.other_cooling!!.isChecked, root?.cooling?.none!!.isChecked
        )

        var heatingType: HeatingType = HeatingType(
            root?.baseboard!!.isChecked, root?.forced_air!!.isChecked,
            root?.geothermal!!.isChecked, root?.heat_pump!!.isChecked,
            root?.radiant!!.isChecked, root?.stove!!.isChecked,
            root?.wall!!.isChecked, root?.heating?.other!!.isChecked
        )
        var utilityDetails: UtilityDetails = UtilityDetails(heatingType, coolingType)

        var buildingDetails: BuildingDetails =
            BuildingDetails(exterior, buildingAminities, outdoorAminities, parking)
        var roomDetails: RoomDetails = RoomDetails(appliances, floorCovering, room)
        property.roomDetails = roomDetails
        property.utilityDetails = utilityDetails
        property.buildingDetails = buildingDetails
        property.homeFacts.isRent = root?.forrent?.isChecked!!
        property.homeFacts.isSale = root?.forsale?.isChecked!!
        val i = Intent(
            context,
            SearchActivity::class.java
        )
        property.homeFacts.homeType = typeCode!!
        i.putExtra("min", mininput.text.toString())
        i.putExtra("max", maxinput.text.toString())
        i.putExtra("argPojo", property)
        startActivity(i)
    }

    fun setUpHomeTypes() {
        var typeSingleFamily: HomeTypes =
            HomeTypes("SIF", resources.getString(R.string.single_family))
        var typeCondo: HomeTypes = HomeTypes("CON", resources.getString(R.string.condo))
        var typeTownHouse: HomeTypes =
            HomeTypes("TOW", resources.getString(R.string.town_house))
        var typeMultiFamily: HomeTypes =
            HomeTypes("MUF", resources.getString(R.string.multi_family))
        var typeApartment: HomeTypes = HomeTypes("APT", resources.getString(R.string.apartment))
        var typeMobileManufactured: HomeTypes =
            HomeTypes("MOB", resources.getString(R.string.mobile_manufactured))
        var typeCoop: HomeTypes = HomeTypes("COU", resources.getString(R.string.coop_unit))
        var typeVacantLand: HomeTypes =
            HomeTypes("VAL", resources.getString(R.string.vacant_land))
        var typeOther: HomeTypes = HomeTypes("OTH", resources.getString(R.string.other))

        list.add(typeSingleFamily)
        list.add(typeCondo)
        list.add(typeTownHouse)
        list.add(typeMultiFamily)
        list.add(typeApartment)
        list.add(typeMobileManufactured)
        list.add(typeCoop)
        list.add(typeVacantLand)
        list.add(typeOther)
        val adapter = RecylerViewAdapter(
            list, this, context!!, false, false
        )
        root?.recyclerView?.apply {
            layoutManager = GridLayoutManager(context, 2)
        }
        root?.recyclerView?.itemAnimator = DefaultItemAnimator()
        root?.recyclerView?.adapter = adapter
    }

    fun setUpExpandableLayouts() {

        root?.arrowdown_hometype?.setOnClickListener {
            if (root?.recyclerView?.visibility == View.VISIBLE) {
                collapse(root?.recyclerView!!)
                root?.arrowdown_hometype?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrowdown_hometype?.setImageResource(R.drawable.right)
                expand(root?.recyclerView!!)
            }
        }
        root?.arrowdown_homefacts?.setOnClickListener {
            if (root?.homeFactLayout?.visibility == View.VISIBLE) {
                collapse(root?.homeFactLayout!!)
                root?.arrowdown_homefacts?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrowdown_homefacts?.setImageResource(R.drawable.right)
                expand(root?.homeFactLayout!!)
            }
        }
        root?.arrowdown_appliances?.setOnClickListener {
            if (root?.applianceslayout?.visibility == View.VISIBLE) {
                collapse(root?.applianceslayout!!)
                root?.arrowdown_appliances?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrowdown_appliances?.setImageResource(R.drawable.right)
                expand(root?.applianceslayout!!)
            }
        }

        root?.arrowdown_floor?.setOnClickListener {
            if (root?.floorcoveringlayout?.visibility == View.VISIBLE) {
                collapse(root?.floorcoveringlayout!!)
                root?.arrowdown_floor?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrowdown_floor?.setImageResource(R.drawable.right)
                expand(root?.floorcoveringlayout!!)
            }
        }

        root?.arrowdown_room?.setOnClickListener {
            if (root?.roomdetailslayout?.visibility == View.VISIBLE) {
                collapse(root?.roomdetailslayout!!)
                root?.arrowdown_room?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrowdown_room?.setImageResource(R.drawable.right)
                expand(root?.roomdetailslayout!!)
            }
        }


        root?.arrowdown_building?.setOnClickListener {
            if (root?.buildingam?.visibility == View.VISIBLE) {
                collapse(root?.buildingam!!)
                root?.arrowdown_building?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrowdown_building?.setImageResource(R.drawable.right)
                expand(root?.buildingam!!)
            }
        }
        root?.arrowdown_exterior?.setOnClickListener {
            if (root?.exteriorlayout?.visibility == View.VISIBLE) {
                root?.arrowdown_exterior?.setImageResource(R.drawable.arrowdown)
                collapse(root?.exteriorlayout!!)
            } else {
                root?.arrowdown_exterior?.setImageResource(R.drawable.right)
                expand(root?.exteriorlayout!!)
            }
        }
        root?.arrow_outdoor?.setOnClickListener {
            if (root?.outdoorlayout?.visibility == View.VISIBLE) {
                collapse(root?.outdoorlayout!!)
                root?.arrow_outdoor?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrow_outdoor?.setImageResource(R.drawable.right)
                expand(root?.outdoorlayout!!)
            }
        }
        root?.arrow_parking?.setOnClickListener {
            if (root?.parkinglayout?.visibility == View.VISIBLE) {
                collapse(root?.parkinglayout!!)
                root?.arrow_parking?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrow_parking?.setImageResource(R.drawable.right)
                expand(root?.parkinglayout!!)

            }
        }
        root?.arrow_cooling?.setOnClickListener {
            if (root?.coolinglayout?.visibility == View.VISIBLE) {
                collapse(root?.coolinglayout!!)
                root?.arrow_cooling?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrow_cooling?.setImageResource(R.drawable.right)
                expand(root?.coolinglayout!!)
            }
        }
        root?.arrow_heating?.setOnClickListener {
            if (root?.heatingtypelayout?.visibility == View.VISIBLE) {
                collapse(root?.heatingtypelayout!!)
                root?.arrow_heating?.setImageResource(R.drawable.arrowdown)
            } else {
                root?.arrow_heating?.setImageResource(R.drawable.right)
                expand(root?.heatingtypelayout!!)
            }
        }
    }

    fun collapse(v: View) {
        try {
            val initialHeight = v.measuredHeight
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration =
                (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        } catch (e: Exception) {

        }

    }

    fun expand(v: View) {
        try {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height = if (interpolatedTime == 1f)
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration =
                (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        } catch (e: Exception) {
        }

    }

    override fun rowClicked(position: Int, position2: Int, imageLayout: ImageView?) {
        var i = 0
        typeCode = ""
        for (homeType in list) {
            var homeTypes = homeType as HomeTypes
            homeTypes.selected = 0
            if (i == position && posSelected != position) {
                posSelected = position
                homeTypes.selected = 1
                typeCode = homeTypes.typeCode
            } else if (i == position) {
                posSelected = -1
                typeCode = ""
            }
            i = i + 1
        }
        root?.recyclerView?.adapter?.notifyDataSetChanged()
    }

    override fun deleteRow(position: Int) {
    }

}
package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.RecylerViewAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.listview_fragment.view.*
import java.util.*

class SearchListPropertyFragment : Fragment(), ListClick {
    var root: View? = null
    lateinit var prop: Property
    lateinit var adapter: RecylerViewAdapter
    var min: String = ""
    var max: String = ""
    var list: MutableList<Any> = mutableListOf<Any>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.listview_fragment, container, false)
        prop = arguments?.getSerializable(SearchListPropertyFragment.ARG_PARAM) as Property
        min = arguments?.getString(SearchListPropertyFragment.ARG_MIN)!!
        max = arguments?.getString(SearchListPropertyFragment.ARG_MAX)!!
        var section = arguments?.getInt(ARG_SECTION_NUMBER)
        if (section == 1) {
            getData(section, prop)
        } else if (section == 2) {
            getData(section, prop)
        } else if (section == 3) {
            getData(section, prop)
        } else {
            getData(section!!, prop)
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_PARAM = "param"
        private const val ARG_MIN = "min"
        private const val ARG_MAX = "max"
        /**   private const val ARG_SECTION_NUMBER = "section_number"
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(
            sectionNumber: Int,
            param: String,
            paramP: Property,
            min: String,
            max: String
        ): SearchListPropertyFragment {
            return SearchListPropertyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putString(ARG_MIN, min)
                    putString(ARG_MAX, max)
                    //   putString(ARG_PARAM, param)
                    putSerializable(ARG_PARAM, paramP)
                }
            }
        }
    }

    private fun getData(param: Int = 0, prop: Property = Property()) {
        val db = FirebaseFirestore.getInstance()
        var docRef = db.collection("properties").whereEqualTo("disabled", "N")
        list.clear()
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var propertyList: MutableList<Property> = document.toObjects(Property::class.java)

                for (pro in propertyList) {
                    if ((prop.homeFacts.homeType.equals("") || prop.homeFacts.homeType.equals(pro.homeFacts.homeType))
                        && (min.trim().equals("") || pro.homeFacts.price!! >= min.toFloat())
                        && (max.trim().equals("") || pro.homeFacts.price!! <= max.toFloat())
                        && (prop.roomDetails.appliances.washer.equals(false) || pro.roomDetails.appliances.washer.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.trashCompactor.equals(false) || pro.roomDetails.appliances.trashCompactor.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.refrigerator.equals(false) || pro.roomDetails.appliances.refrigerator.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.rangeoven.equals(false) || pro.roomDetails.appliances.rangeoven.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.microwave.equals(false) || pro.roomDetails.appliances.microwave.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.disposal.equals(false) || pro.roomDetails.appliances.disposal.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.freezer.equals(false) || pro.roomDetails.appliances.freezer.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.dryer.equals(false) || pro.roomDetails.appliances.dryer.equals(
                            true
                        ))
                        && (prop.roomDetails.appliances.dishWasher.equals(false) || pro.roomDetails.appliances.dishWasher.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.carpet.equals(false) || pro.roomDetails.floorCovering.carpet.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.hardwood.equals(false) || pro.roomDetails.floorCovering.hardwood.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.concrete.equals(false) || pro.roomDetails.floorCovering.concrete.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.laminate.equals(false) || pro.roomDetails.floorCovering.laminate.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.slate.equals(false) || pro.roomDetails.floorCovering.slate.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.softwood.equals(false) || pro.roomDetails.floorCovering.softwood.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.linoleum.equals(false) || pro.roomDetails.floorCovering.linoleum.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.tile.equals(false) || pro.roomDetails.floorCovering.tile.equals(
                            true
                        ))
                        && (prop.roomDetails.floorCovering.other.equals(false) || pro.roomDetails.floorCovering.other.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.breakfast.equals(false) || pro.roomDetails.rooms.breakfast.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.dinning.equals(false) || pro.roomDetails.rooms.dinning.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.family.equals(false) || pro.roomDetails.rooms.family.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.laundry.equals(false) || pro.roomDetails.rooms.laundry.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.library.equals(false) || pro.roomDetails.rooms.library.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.masterBath.equals(false) || pro.roomDetails.rooms.masterBath.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.mud.equals(false) || pro.roomDetails.rooms.mud.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.office.equals(false) || pro.roomDetails.rooms.office.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.pantry.equals(false) || pro.roomDetails.rooms.pantry.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.recreation.equals(false) || pro.roomDetails.rooms.recreation.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.workshop.equals(false) || pro.roomDetails.rooms.workshop.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.solarium.equals(false) || pro.roomDetails.rooms.solarium.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.sun.equals(false) || pro.roomDetails.rooms.sun.equals(
                            true
                        ))
                        && (prop.roomDetails.rooms.walkInCloset.equals(false) || pro.roomDetails.rooms.walkInCloset.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.brick.equals(false) || pro.buildingDetails.exterior.brick.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.cement.equals(false) || pro.buildingDetails.exterior.cement.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.composition.equals(false) || pro.buildingDetails.exterior.composition.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.metal.equals(false) || pro.buildingDetails.exterior.metal.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.shingle.equals(false) || pro.buildingDetails.exterior.shingle.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.stone.equals(false) || pro.buildingDetails.exterior.stone.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.stucco.equals(false) || pro.buildingDetails.exterior.stucco.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.vinyl.equals(false) || pro.buildingDetails.exterior.vinyl.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.wood.equals(false) || pro.buildingDetails.exterior.wood.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.woodProducts.equals(false) || pro.buildingDetails.exterior.woodProducts.equals(
                            true
                        ))
                        && (prop.buildingDetails.exterior.other.equals(false) || pro.buildingDetails.exterior.other.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.basketballCourt.equals(false) || pro.buildingDetails.buildingAminities.basketballCourt.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.disabledAccess.equals(false) || pro.buildingDetails.buildingAminities.disabledAccess.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.doorman.equals(false) || pro.buildingDetails.buildingAminities.doorman.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.elevator.equals(false) || pro.buildingDetails.buildingAminities.elevator.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.fitnessCenter.equals(false) || pro.buildingDetails.buildingAminities.fitnessCenter.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.sportsCourt.equals(false) || pro.buildingDetails.buildingAminities.sportsCourt.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.storage.equals(false) || pro.buildingDetails.buildingAminities.storage.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.tennisCourt.equals(false) || pro.buildingDetails.buildingAminities.tennisCourt.equals(
                            true
                        ))
                        && (prop.buildingDetails.buildingAminities.nearTransportation.equals(false) || pro.buildingDetails.buildingAminities.nearTransportation.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.balconyPatio.equals(false) || pro.buildingDetails.outdoorAminities.balconyPatio.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.lawn.equals(false) || pro.buildingDetails.outdoorAminities.lawn.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.barbecueArea.equals(false) || pro.buildingDetails.outdoorAminities.barbecueArea.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.pond.equals(false) || pro.buildingDetails.outdoorAminities.pond.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.deck.equals(false) || pro.buildingDetails.outdoorAminities.deck.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.pool.equals(false) || pro.buildingDetails.outdoorAminities.pool.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.dock.equals(false) || pro.buildingDetails.outdoorAminities.dock.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.porch.equals(false) || pro.buildingDetails.outdoorAminities.porch.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.fencedYard.equals(false) || pro.buildingDetails.outdoorAminities.fencedYard.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.rvParking.equals(false) || pro.buildingDetails.outdoorAminities.rvParking.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.garden.equals(false) || pro.buildingDetails.outdoorAminities.garden.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.sauna.equals(false) || pro.buildingDetails.outdoorAminities.sauna.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.greenHouse.equals(false) || pro.buildingDetails.outdoorAminities.greenHouse.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.sprinkerlSystem.equals(false) || pro.buildingDetails.outdoorAminities.sprinkerlSystem.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.hotTubSpa.equals(false) || pro.buildingDetails.outdoorAminities.hotTubSpa.equals(
                            true
                        ))
                        && (prop.buildingDetails.outdoorAminities.waterfront.equals(false) || pro.buildingDetails.outdoorAminities.waterfront.equals(
                            true
                        ))
                        && (prop.buildingDetails.parking.carport.equals(false) || pro.buildingDetails.parking.carport.equals(
                            true
                        ))
                        && (prop.buildingDetails.parking.onStreet.equals(false) || pro.buildingDetails.parking.onStreet.equals(
                            true
                        ))
                        && (prop.buildingDetails.parking.offStreet.equals(false) || pro.buildingDetails.parking.offStreet.equals(
                            true
                        ))
                        && (prop.buildingDetails.parking.garageAttached.equals(false) || pro.buildingDetails.parking.garageAttached.equals(
                            true
                        ))
                        && (prop.buildingDetails.parking.garageDetached.equals(false) || pro.buildingDetails.parking.garageDetached.equals(
                            true
                        ))
                        && (prop.buildingDetails.parking.none.equals(false) || pro.buildingDetails.parking.none.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.baseboard.equals(false) || pro.utilityDetails.heatingType.baseboard.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.forcedAir.equals(false) || pro.utilityDetails.heatingType.forcedAir.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.geoThermal.equals(false) || pro.utilityDetails.heatingType.geoThermal.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.heatPump.equals(false) || pro.utilityDetails.heatingType.heatPump.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.radiant.equals(false) || pro.utilityDetails.heatingType.radiant.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.stove.equals(false) || pro.utilityDetails.heatingType.stove.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.wall.equals(false) || pro.utilityDetails.heatingType.wall.equals(
                            true
                        ))
                        && (prop.utilityDetails.heatingType.other.equals(false) || pro.utilityDetails.heatingType.other.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.central.equals(false) || pro.utilityDetails.coolingType.central.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.evaporative.equals(false) || pro.utilityDetails.coolingType.evaporative.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.geoThermal.equals(false) || pro.utilityDetails.coolingType.geoThermal.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.refrigeration.equals(false) || pro.utilityDetails.coolingType.refrigeration.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.solar.equals(false) || pro.utilityDetails.coolingType.solar.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.wall.equals(false) || pro.utilityDetails.coolingType.wall.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.other.equals(false) || pro.utilityDetails.coolingType.other.equals(
                            true
                        ))
                        && (prop.utilityDetails.coolingType.none.equals(false) || pro.utilityDetails.coolingType.none.equals(
                            true
                        ))
                    ) {
                        list.add(pro)
                    }
                }
                root?.text?.visibility = View.VISIBLE

                if (param == 3) {
                    val customComparator = object : Comparator<Property> {
                        override fun compare(a: Property, b: Property): Int {
                            System.out.println("---------c" + (a.homeFacts.price!! - b.homeFacts.price!!))
                            if (a == null && b == null) {
                                return 0
                            } else if (a == null) {
                                return -1
                            } else if (b == null) {
                                return 1
                            } else if ((a.homeFacts.price!! - b.homeFacts.price!!) > 0.0)
                                return 1
                            else
                                return -1
                        }
                    }
                    Collections.sort(list as MutableList<Property>, customComparator)
                }

                adapter = RecylerViewAdapter(
                    list, this, root?.context!!, true
                )
                root?.recyclerView?.apply {
                    val layoutManager1 = GridLayoutManager(context, 2)
                    layoutManager1.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            if (position == (list.size - 1) && position % 2 == 0)
                                return 2
                            else
                                return 1
                        }
                    }
                    layoutManager = layoutManager1
                }

                // root?.recyclerView?.layoutManager = LinearLayoutManager(root?.context)
                root?.recyclerView?.itemAnimator = DefaultItemAnimator()
                root?.recyclerView?.adapter = adapter
                if (list.size > 0)
                    root?.text?.visibility = View.GONE
                else
                    root?.text?.visibility = View.VISIBLE

            } else {
            }
        }
            .addOnFailureListener { exception ->
            }

    }

    override fun rowClicked(position: Int, position2: Int, imageLayout: ImageView?) {
    }

    override fun deleteRow(position: Int) {
    }

}
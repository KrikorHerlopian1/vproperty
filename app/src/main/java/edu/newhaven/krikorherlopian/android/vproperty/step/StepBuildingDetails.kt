package edu.newhaven.krikorherlopian.android.vproperty.step


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.model.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.building_amenities.view.*
import kotlinx.android.synthetic.main.exterior.view.*
import kotlinx.android.synthetic.main.outdoor_amenities.view.*
import kotlinx.android.synthetic.main.parking.view.*


class StepBuildingDetails(context: Context, listener: OnNavigationBarListener, var prop: Property) :
    FrameLayout(context),
    Step {
    internal var view: View? = null
    @Nullable
    private var onNavigationBarListener: OnNavigationBarListener? = null

    init {
        init(context, listener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val c = context
        if (c is OnNavigationBarListener) {
            this.onNavigationBarListener = c
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.onNavigationBarListener = null
    }

    private fun init(context: Context, listener: OnNavigationBarListener) {
        val layoutView = LayoutInflater.from(context)
            .inflate(R.layout.fragment_step_building_details, this, true)
        try {
            onNavigationBarListener = listener
            view = layoutView

            view?.brick!!.isChecked = prop.buildingDetails.exterior.brick
            view?.cement!!.isChecked = prop.buildingDetails.exterior.cement
            view?.composition!!.isChecked = prop.buildingDetails.exterior.composition
            view?.metal!!.isChecked = prop.buildingDetails.exterior.metal
            view?.shingle!!.isChecked = prop.buildingDetails.exterior.shingle
            view?.stone!!.isChecked = prop.buildingDetails.exterior.stone
            view?.stucco!!.isChecked = prop.buildingDetails.exterior.stucco
            view?.vinyl!!.isChecked = prop.buildingDetails.exterior.vinyl
            view?.wood!!.isChecked = prop.buildingDetails.exterior.wood
            view?.wood_products!!.isChecked = prop.buildingDetails.exterior.woodProducts
            view?.other!!.isChecked = prop.buildingDetails.exterior.other

            view?.basketball_court!!.isChecked =
                prop.buildingDetails.buildingAminities.basketballCourt
            view?.disabled_access!!.isChecked =
                prop.buildingDetails.buildingAminities.disabledAccess
            view?.doorman!!.isChecked = prop.buildingDetails.buildingAminities.doorman
            view?.elevator!!.isChecked = prop.buildingDetails.buildingAminities.elevator
            view?.fitness_center!!.isChecked = prop.buildingDetails.buildingAminities.fitnessCenter
            view?.sports_court!!.isChecked = prop.buildingDetails.buildingAminities.sportsCourt
            view?.storage!!.isChecked = prop.buildingDetails.buildingAminities.storage
            view?.tennis_court!!.isChecked = prop.buildingDetails.buildingAminities.tennisCourt
            view?.near_transportation!!.isChecked =
                prop.buildingDetails.buildingAminities.nearTransportation

            view?.balcony_patio!!.isChecked = prop.buildingDetails.outdoorAminities.balconyPatio
            view?.lawn!!.isChecked = prop.buildingDetails.outdoorAminities.lawn
            view?.barbecue_area!!.isChecked = prop.buildingDetails.outdoorAminities.barbecueArea
            view?.pond!!.isChecked = prop.buildingDetails.outdoorAminities.pond
            view?.deck!!.isChecked = prop.buildingDetails.outdoorAminities.deck
            view?.pool!!.isChecked = prop.buildingDetails.outdoorAminities.pool
            view?.dock!!.isChecked = prop.buildingDetails.outdoorAminities.dock
            view?.porch!!.isChecked = prop.buildingDetails.outdoorAminities.porch
            view?.fenced_yard!!.isChecked = prop.buildingDetails.outdoorAminities.fencedYard
            view?.rv_parking!!.isChecked = prop.buildingDetails.outdoorAminities.rvParking
            view?.garden!!.isChecked = prop.buildingDetails.outdoorAminities.garden
            view?.sauna!!.isChecked = prop.buildingDetails.outdoorAminities.sauna
            view?.greenhouse!!.isChecked = prop.buildingDetails.outdoorAminities.greenHouse
            view?.sprinkler_system!!.isChecked =
                prop.buildingDetails.outdoorAminities.sprinkerlSystem
            view?.hottubspa!!.isChecked = prop.buildingDetails.outdoorAminities.hotTubSpa
            view?.waterfront!!.isChecked = prop.buildingDetails.outdoorAminities.waterfront

            view?.carport!!.isChecked = prop.buildingDetails.parking.carport
            view?.on_street!!.isChecked = prop.buildingDetails.parking.onStreet
            view?.off_street!!.isChecked = prop.buildingDetails.parking.offStreet
            view?.garage_attached!!.isChecked = prop.buildingDetails.parking.garageAttached
            view?.garage_detached!!.isChecked = prop.buildingDetails.parking.garageDetached
            view?.none!!.isChecked = prop.buildingDetails.parking.none

        } catch (e: Exception) {
        }

    }

    override fun verifyStep(): VerificationError? {
        try {

            var exterior: Exterior = Exterior(
                view?.brick!!.isChecked, view?.cement!!.isChecked,
                view?.composition!!.isChecked, view?.metal!!.isChecked,
                view?.shingle!!.isChecked, view?.stone!!.isChecked,
                view?.stucco!!.isChecked, view?.vinyl!!.isChecked,
                view?.wood!!.isChecked, view?.wood_products!!.isChecked,
                view?.other!!.isChecked
            )

            var buildingAminities = BuildingAminities(
                view?.basketball_court!!.isChecked,
                view?.disabled_access!!.isChecked,
                view?.doorman!!.isChecked,
                view?.elevator!!.isChecked,
                view?.fitness_center!!.isChecked,
                view?.sports_court!!.isChecked,
                view?.storage!!.isChecked,
                view?.tennis_court!!.isChecked,
                view?.near_transportation!!.isChecked
            )

            var outdoorAminities = OutdoorAminities(
                view?.balcony_patio!!.isChecked, view?.lawn!!.isChecked,
                view?.barbecue_area!!.isChecked, view?.pond!!.isChecked,
                view?.deck!!.isChecked, view?.pool!!.isChecked,
                view?.dock!!.isChecked, view?.porch!!.isChecked,
                view?.fenced_yard!!.isChecked, view?.rv_parking!!.isChecked,
                view?.garden!!.isChecked, view?.sauna!!.isChecked,
                view?.greenhouse!!.isChecked, view?.sprinkler_system!!.isChecked,
                view?.hottubspa!!.isChecked, view?.waterfront!!.isChecked
            )

            var parking: Parking = Parking(
                view?.carport!!.isChecked, view?.on_street!!.isChecked,
                view?.off_street!!.isChecked, view?.garage_attached!!.isChecked,
                view?.garage_detached!!.isChecked, view?.none!!.isChecked
            )

            var buildingDetails: BuildingDetails =
                BuildingDetails(exterior, buildingAminities, outdoorAminities, parking)
            onNavigationBarListener?.addBuildingDetails(buildingDetails)
        } catch (e: Exception) {
        }

        return null
    }

    override fun onSelected() {
    }

    override fun onError(@NonNull error: VerificationError) {
        Toasty.error(
            context,
            error.errorMessage,
            Toast.LENGTH_SHORT,
            true
        ).show()
    }
}
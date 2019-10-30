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
import kotlinx.android.synthetic.main.fragment_step_building_details.view.*


class StepBuildingDetails(context: Context, listener: OnNavigationBarListener) :
    FrameLayout(context),
    Step {
    internal var ms: View? = null
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
        val v = LayoutInflater.from(context)
            .inflate(R.layout.fragment_step_building_details, this, true)
        try {
            onNavigationBarListener = listener
            ms = v
        } catch (e: Exception) {
        }

    }

    override fun verifyStep(): VerificationError? {
        try {

            var exterior: Exterior = Exterior(
                ms?.brick!!.isChecked, ms?.cement!!.isChecked,
                ms?.composition!!.isChecked, ms?.metal!!.isChecked,
                ms?.shingle!!.isChecked, ms?.stone!!.isChecked,
                ms?.stucco!!.isChecked, ms?.vinyl!!.isChecked,
                ms?.wood!!.isChecked, ms?.wood_products!!.isChecked,
                ms?.other!!.isChecked
            )

            var buildingAminities = BuildingAminities(
                ms?.basketball_court!!.isChecked,
                ms?.disabled_access!!.isChecked,
                ms?.doorman!!.isChecked,
                ms?.elevator!!.isChecked,
                ms?.fitness_center!!.isChecked,
                ms?.sports_court!!.isChecked,
                ms?.storage!!.isChecked,
                ms?.tennis_court!!.isChecked,
                ms?.near_transportation!!.isChecked
            )

            var outdoorAminities = OutdoorAminities(
                ms?.balcony_patio!!.isChecked, ms?.lawn!!.isChecked,
                ms?.barbecue_area!!.isChecked, ms?.pond!!.isChecked,
                ms?.deck!!.isChecked, ms?.pool!!.isChecked,
                ms?.dock!!.isChecked, ms?.porch!!.isChecked,
                ms?.fenced_yard!!.isChecked, ms?.rv_parking!!.isChecked,
                ms?.garden!!.isChecked, ms?.sauna!!.isChecked,
                ms?.greenhouse!!.isChecked, ms?.sprinkler_system!!.isChecked,
                ms?.hottubspa!!.isChecked, ms?.waterfront!!.isChecked
            )

            var parking: Parking = Parking(
                ms?.carport!!.isChecked, ms?.on_street!!.isChecked,
                ms?.off_street!!.isChecked, ms?.garage_attached!!.isChecked,
                ms?.garage_detached!!.isChecked, ms?.none!!.isChecked
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
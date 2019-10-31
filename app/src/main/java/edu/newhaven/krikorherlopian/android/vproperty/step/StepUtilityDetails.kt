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
import edu.newhaven.krikorherlopian.android.vproperty.model.CoolingType
import edu.newhaven.krikorherlopian.android.vproperty.model.HeatingType
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import edu.newhaven.krikorherlopian.android.vproperty.model.UtilityDetails
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_step_utility_details.view.*


class StepUtilityDetails(context: Context, listener: OnNavigationBarListener, var prop: Property) :
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
        val v =
            LayoutInflater.from(context).inflate(R.layout.fragment_step_utility_details, this, true)
        try {
            onNavigationBarListener = listener
            ms = v

            ms?.central!!.isChecked = prop.utilityDetails.coolingType.central
            ms?.evaporative!!.isChecked = prop.utilityDetails.coolingType.evaporative
            ms?.geothermal_cooling!!.isChecked = prop.utilityDetails.coolingType.geoThermal
            ms?.refrigeration!!.isChecked = prop.utilityDetails.coolingType.refrigeration
            ms?.solar!!.isChecked = prop.utilityDetails.coolingType.solar
            ms?.wall_cooling!!.isChecked = prop.utilityDetails.coolingType.wall
            ms?.other_cooling!!.isChecked = prop.utilityDetails.coolingType.other
            ms?.none!!.isChecked = prop.utilityDetails.coolingType.none

            ms?.baseboard!!.isChecked = prop.utilityDetails.heatingType.baseboard
            ms?.forced_air!!.isChecked = prop.utilityDetails.heatingType.forcedAir
            ms?.geothermal!!.isChecked = prop.utilityDetails.heatingType.geoThermal
            ms?.heat_pump!!.isChecked = prop.utilityDetails.heatingType.heatPump
            ms?.other!!.isChecked = prop.utilityDetails.heatingType.other
            ms?.radiant!!.isChecked = prop.utilityDetails.heatingType.radiant
            ms?.stove!!.isChecked = prop.utilityDetails.heatingType.stove
            ms?.wall!!.isChecked = prop.utilityDetails.heatingType.wall
        } catch (e: Exception) {
        }

    }

    override fun verifyStep(): VerificationError? {
        try {
            var coolingType: CoolingType = CoolingType(
                ms?.central!!.isChecked, ms?.evaporative!!.isChecked,
                ms?.geothermal_cooling!!.isChecked, ms?.refrigeration!!.isChecked,
                ms?.solar!!.isChecked, ms?.wall_cooling!!.isChecked,
                ms?.other_cooling!!.isChecked, ms?.none!!.isChecked
            )

            var heatingType: HeatingType = HeatingType(
                ms?.baseboard!!.isChecked, ms?.forced_air!!.isChecked,
                ms?.geothermal!!.isChecked, ms?.heat_pump!!.isChecked,
                ms?.radiant!!.isChecked, ms?.stove!!.isChecked,
                ms?.wall!!.isChecked, ms?.other!!.isChecked
            )
            var utilityDetails: UtilityDetails = UtilityDetails(heatingType, coolingType)
            onNavigationBarListener?.addUtilityDetails(utilityDetails)
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
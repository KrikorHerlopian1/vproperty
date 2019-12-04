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
import kotlinx.android.synthetic.main.cooling_type.view.*
import kotlinx.android.synthetic.main.fragment_step_utility_details.view.*
import kotlinx.android.synthetic.main.heating_type.view.*


class StepUtilityDetails(context: Context, listener: OnNavigationBarListener, var prop: Property) :
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
        val v =
            LayoutInflater.from(context).inflate(R.layout.fragment_step_utility_details, this, true)
        try {
            onNavigationBarListener = listener
            view = v

            view?.coolingtypelayout?.central!!.isChecked = prop.utilityDetails.coolingType.central
            view?.coolingtypelayout?.evaporative!!.isChecked =
                prop.utilityDetails.coolingType.evaporative
            view?.coolingtypelayout?.geothermal_cooling!!.isChecked =
                prop.utilityDetails.coolingType.geoThermal
            view?.coolingtypelayout?.refrigeration!!.isChecked =
                prop.utilityDetails.coolingType.refrigeration
            view?.coolingtypelayout?.solar!!.isChecked = prop.utilityDetails.coolingType.solar
            view?.coolingtypelayout?.wall_cooling!!.isChecked = prop.utilityDetails.coolingType.wall
            view?.coolingtypelayout?.other_cooling!!.isChecked =
                prop.utilityDetails.coolingType.other
            view?.coolingtypelayout?.none!!.isChecked = prop.utilityDetails.coolingType.none


            view?.heatingtypelayout?.baseboard!!.isChecked =
                prop.utilityDetails.heatingType.baseboard
            view?.heatingtypelayout?.forced_air!!.isChecked =
                prop.utilityDetails.heatingType.forcedAir
            view?.heatingtypelayout?.geothermal!!.isChecked =
                prop.utilityDetails.heatingType.geoThermal
            view?.heatingtypelayout?.heat_pump!!.isChecked =
                prop.utilityDetails.heatingType.heatPump
            view?.heatingtypelayout?.other!!.isChecked = prop.utilityDetails.heatingType.other
            view?.heatingtypelayout?.radiant!!.isChecked = prop.utilityDetails.heatingType.radiant
            view?.heatingtypelayout?.stove!!.isChecked = prop.utilityDetails.heatingType.stove
            view?.heatingtypelayout?.wall!!.isChecked = prop.utilityDetails.heatingType.wall
        } catch (e: Exception) {
        }

    }

    override fun verifyStep(): VerificationError? {
        try {
            var coolingType: CoolingType = CoolingType(
                view?.central!!.isChecked, view?.evaporative!!.isChecked,
                view?.geothermal_cooling!!.isChecked, view?.refrigeration!!.isChecked,
                view?.solar!!.isChecked, view?.wall_cooling!!.isChecked,
                view?.other_cooling!!.isChecked, view?.none!!.isChecked
            )

            var heatingType: HeatingType = HeatingType(
                view?.baseboard!!.isChecked, view?.forced_air!!.isChecked,
                view?.geothermal!!.isChecked, view?.heat_pump!!.isChecked,
                view?.radiant!!.isChecked, view?.stove!!.isChecked,
                view?.wall!!.isChecked, view?.other!!.isChecked
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
package edu.newhaven.krikorherlopian.android.vproperty.step

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.RecylerViewAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.model.HomeTypes
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_step_home_type.view.*

class StepHomeType(context: Context, listener: OnNavigationBarListener, var property: Property) :
    FrameLayout(context),
    Step, ListClick {
    internal var ms: View? = null
    var posSelected = -1
    var typeCode: String? = ""

    var list: MutableList<Any> = mutableListOf<Any>()
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
        val v = LayoutInflater.from(context).inflate(R.layout.fragment_step_home_type, this, true)
        try {
            onNavigationBarListener = listener
            ms = v
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

            if (property.homeFacts.homeType.equals("SIF")) {
                typeCode = property.homeFacts.homeType
                typeSingleFamily.selected = 1
            } else if (property.homeFacts.homeType.equals("CON")) {
                typeCode = property.homeFacts.homeType
                typeCondo.selected = 1
            } else if (property.homeFacts.homeType.equals("TOW")) {
                typeCode = property.homeFacts.homeType
                typeTownHouse.selected = 1
            } else if (property.homeFacts.homeType.equals("MUF")) {
                typeCode = property.homeFacts.homeType
                typeMultiFamily.selected = 1
            } else if (property.homeFacts.homeType.equals("APT")) {
                typeCode = property.homeFacts.homeType
                typeApartment.selected = 1
            } else if (property.homeFacts.homeType.equals("MOB")) {
                typeCode = property.homeFacts.homeType
                typeMobileManufactured.selected = 1
            } else if (property.homeFacts.homeType.equals("COU")) {
                typeCode = property.homeFacts.homeType
                typeCoop.selected = 1
            } else if (property.homeFacts.homeType.equals("VAL")) {
                typeCode = property.homeFacts.homeType
                typeVacantLand.selected = 1
            } else if (property.homeFacts.homeType.equals("OTH")) {
                typeCode = property.homeFacts.homeType
                typeOther.selected = 1
            }
            val adapter = RecylerViewAdapter(
                list, this, context
            )
            //  v?.recyclerView?.layoutManager = LinearLayoutManager(context)
            v?.recyclerView?.apply {
                val layoutManager1 = GridLayoutManager(context, 2)
                layoutManager = layoutManager1
            }
            v?.recyclerView?.itemAnimator = DefaultItemAnimator()
            v?.recyclerView?.adapter = adapter
        } catch (e: Exception) {
        }

    }

    override fun rowClicked(position: Int, position2: Int, imageLayout: ImageView?) {
        var i = 0
        for (homeType in list) {
            var homeTypes = homeType as HomeTypes
            homeTypes.selected = 0
            if (i == position) {
                homeTypes.selected = 1
                typeCode = homeTypes.typeCode
            }
            i = i + 1
        }
        posSelected = position
        ms?.recyclerView?.adapter?.notifyDataSetChanged()
        onNavigationBarListener?.addHomeType(typeCode!!)
    }

    override fun verifyStep(): VerificationError? {
        try {
            if (posSelected == -1) {
                return VerificationError(resources.getString(R.string.choose_home_type))
            } else {
                onNavigationBarListener?.addHomeType(typeCode!!, -1)
            }
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

    override fun deleteRow(position: Int) {
    }
}
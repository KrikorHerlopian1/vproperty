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
import androidx.recyclerview.widget.LinearLayoutManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.HomeTypeAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.model.HomeTypePair
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

    var list: MutableList<HomeTypePair> = mutableListOf<HomeTypePair>()
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
            var propertType1: HomeTypes =
                HomeTypes("SIF", resources.getString(R.string.single_family))
            var propertType2: HomeTypes = HomeTypes("CON", resources.getString(R.string.condo))
            list.add(HomeTypePair(propertType1, propertType2))

            var propertType3: HomeTypes = HomeTypes("TOW", resources.getString(R.string.town_house))
            var propertType4: HomeTypes =
                HomeTypes("MUF", resources.getString(R.string.multi_family))
            list.add(HomeTypePair(propertType3, propertType4))

            var propertType5: HomeTypes = HomeTypes("APT", resources.getString(R.string.apartment))
            var propertType6: HomeTypes =
                HomeTypes("MOB", resources.getString(R.string.mobile_manufactured))
            list.add(HomeTypePair(propertType5, propertType6))

            var propertType7: HomeTypes = HomeTypes("COU", resources.getString(R.string.coop_unit))
            var propertType8: HomeTypes =
                HomeTypes("VAL", resources.getString(R.string.vacant_land))
            list.add(HomeTypePair(propertType7, propertType8))

            var propertType9: HomeTypes = HomeTypes("OTH", resources.getString(R.string.other))
            list.add(HomeTypePair(propertType9, null))

            if (property.homeFacts.homeType.equals("SIF")) {
                typeCode = property.homeFacts.homeType
                posSelected = 1
                propertType1.selected = 1
            } else if (property.homeFacts.homeType.equals("CON")) {
                typeCode = property.homeFacts.homeType
                posSelected = 1
                propertType2.selected = 1
            } else if (property.homeFacts.homeType.equals("TOW")) {
                typeCode = property.homeFacts.homeType
                propertType3.selected = 1
                posSelected = 2
            } else if (property.homeFacts.homeType.equals("MUF")) {
                typeCode = property.homeFacts.homeType
                propertType4.selected = 1
                posSelected = 2
            } else if (property.homeFacts.homeType.equals("APT")) {
                typeCode = property.homeFacts.homeType
                propertType5.selected = 1
                posSelected = 3
            } else if (property.homeFacts.homeType.equals("MOB")) {
                typeCode = property.homeFacts.homeType
                propertType6.selected = 1
                posSelected = 3
            } else if (property.homeFacts.homeType.equals("COU")) {
                typeCode = property.homeFacts.homeType
                propertType7.selected = 1
                posSelected = 4
            } else if (property.homeFacts.homeType.equals("VAL")) {
                typeCode = property.homeFacts.homeType
                propertType8.selected = 1
                posSelected = 4
            } else if (property.homeFacts.homeType.equals("OTH")) {
                typeCode = property.homeFacts.homeType
                propertType9.selected = 1
                posSelected = 5
            }
            val adapter = HomeTypeAdapter(
                list, this
            )
            v?.recyclerView?.layoutManager = LinearLayoutManager(context)
            v?.recyclerView?.itemAnimator = DefaultItemAnimator()
            v?.recyclerView?.adapter = adapter
        } catch (e: Exception) {
        }

    }

    override fun rowClicked(position: Int, position2: Int, imageLayout: ImageView?) {
        for (homeTypePair in list) {
            homeTypePair.homeType1?.selected = 0
            homeTypePair.homeType2?.selected = 0
        }
        if (position2 == 1) {
            typeCode = list.get(position).homeType1?.typeCode
            list.get(position).homeType1?.selected = 1
        } else {
            typeCode = list.get(position).homeType2?.typeCode
            list.get(position).homeType2?.selected = 1
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
}
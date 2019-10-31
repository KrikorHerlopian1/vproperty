package edu.newhaven.krikorherlopian.android.vproperty.adapter

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractStepAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import edu.newhaven.krikorherlopian.android.vproperty.step.*


class MyStepperAdapter(
    context: Context,
    internal var listener: OnNavigationBarListener,
    var property: Property
) :
    AbstractStepAdapter(context) {

    private val pages = SparseArray<Step>()

    fun createStepHomeType(position: Int): StepHomeType {
        return StepHomeType(context, listener, property)

    }

    fun createStepHomeAddress(position: Int): StepHomeAddress {
        return StepHomeAddress(context, listener, property)

    }

    fun createStepHomeFacts(position: Int): StepHomeFacts {
        return StepHomeFacts(context, listener, property)

    }

    fun createStepRoomDetails(position: Int): StepRoomDetails {
        return StepRoomDetails(context, listener, property)

    }

    fun createStepBuildingDetails(position: Int): StepBuildingDetails {
        return StepBuildingDetails(context, listener, property)

    }

    fun createStepUtilityDetails(position: Int): StepUtilityDetails {
        return StepUtilityDetails(context, listener, property)

    }

    fun createStepMoreInformation(position: Int): StepMoreInformation {
        return StepMoreInformation(context, listener, property)

    }

    override fun getCount(): Int {
        return 7
    }

    override fun findStep(p0: ViewPager?, p1: Int): Step? {
        return if (pages.size() > 0) pages.get(p1) else null
    }

    override fun createStep(position: Int): Step {
        return createStepHomeType(0)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        var step: Step? = pages.get(position)
        if (step == null) {
            if (position == 0)
                step = createStepHomeType(position)
            else if (position == 1)
                step = createStepHomeAddress(position)
            else if (position == 2)
                step = createStepHomeFacts(position)
            else if (position == 3)
                step = createStepRoomDetails(position)
            else if (position == 4)
                step = createStepBuildingDetails(position)
            else if (position == 5)
                step = createStepUtilityDetails(position)
            else if (position == 6)
                step = createStepMoreInformation(position)
            pages.put(position, step)
        }

        val stepView = step as View?
        container.addView(stepView)

        return stepView!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}
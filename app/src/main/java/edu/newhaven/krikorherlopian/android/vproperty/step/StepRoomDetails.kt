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
import edu.newhaven.krikorherlopian.android.vproperty.model.Appliances
import edu.newhaven.krikorherlopian.android.vproperty.model.FloorCovering
import edu.newhaven.krikorherlopian.android.vproperty.model.Room
import edu.newhaven.krikorherlopian.android.vproperty.model.RoomDetails
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_step_room_details.view.*


class StepRoomDetails(context: Context, listener: OnNavigationBarListener) : FrameLayout(context),
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
            LayoutInflater.from(context).inflate(R.layout.fragment_step_room_details, this, true)
        try {
            onNavigationBarListener = listener
            ms = v
        } catch (e: Exception) {
        }

    }

    override fun verifyStep(): VerificationError? {
        try {
            var appliances: Appliances = Appliances(
                ms?.dishwasher!!.isChecked, ms?.dryer!!.isChecked,
                ms?.freezer!!.isChecked, ms?.garbage_disposal!!.isChecked,
                ms?.microwave!!.isChecked, ms?.range_oven!!.isChecked,
                ms?.refrigerator!!.isChecked, ms?.washer!!.isChecked,
                ms?.trash_compactor!!.isChecked
            )
            var floorCovering: FloorCovering = FloorCovering(
                ms?.carpet!!.isChecked, ms?.concrete!!.isChecked,
                ms?.hardwood!!.isChecked, ms?.laminate!!.isChecked,
                ms?.slate!!.isChecked, ms?.softwood!!.isChecked,
                ms?.linoleum!!.isChecked, ms?.tile!!.isChecked,
                ms?.other!!.isChecked
            )
            var room: Room = Room(
                ms?.breakfast!!.isChecked, ms?.dinning!!.isChecked,
                ms?.family!!.isChecked, ms?.laundry!!.isChecked,
                ms?.library!!.isChecked, ms?.master_bath!!.isChecked,
                ms?.mud!!.isChecked, ms?.office!!.isChecked,
                ms?.pantry!!.isChecked, ms?.recreation!!.isChecked,
                ms?.workshop!!.isChecked, ms?.solarium!!.isChecked,
                ms?.sun!!.isChecked, ms?.walk_in_closet!!.isChecked
            )

            var roomDetails: RoomDetails = RoomDetails(appliances, floorCovering, room)
            onNavigationBarListener?.addRoomDetails(roomDetails)
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
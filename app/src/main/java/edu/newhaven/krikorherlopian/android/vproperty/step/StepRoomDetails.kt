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
import kotlinx.android.synthetic.main.fragment_step_room_details.view.*


class StepRoomDetails(context: Context, listener: OnNavigationBarListener, var prop: Property) :
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
            LayoutInflater.from(context).inflate(R.layout.fragment_step_room_details, this, true)
        try {
            onNavigationBarListener = listener
            ms = v

            ms?.dishwasher!!.isChecked = prop.roomDetails.appliances.dishWasher
            ms?.washer!!.isChecked = prop.roomDetails.appliances.washer
            ms?.garbage_disposal!!.isChecked = prop.roomDetails.appliances.disposal
            ms?.dryer!!.isChecked = prop.roomDetails.appliances.dryer
            ms?.freezer!!.isChecked = prop.roomDetails.appliances.freezer
            ms?.refrigerator!!.isChecked = prop.roomDetails.appliances.refrigerator
            ms?.microwave!!.isChecked = prop.roomDetails.appliances.microwave
            ms?.range_oven!!.isChecked = prop.roomDetails.appliances.rangeoven
            ms?.trash_compactor!!.isChecked = prop.roomDetails.appliances.trashCompactor

            ms?.carpet!!.isChecked = prop.roomDetails.floorCovering.carpet
            ms?.concrete!!.isChecked = prop.roomDetails.floorCovering.concrete
            ms?.hardwood!!.isChecked = prop.roomDetails.floorCovering.hardwood
            ms?.laminate!!.isChecked = prop.roomDetails.floorCovering.laminate
            ms?.linoleum!!.isChecked = prop.roomDetails.floorCovering.linoleum
            ms?.slate!!.isChecked = prop.roomDetails.floorCovering.slate
            ms?.softwood!!.isChecked = prop.roomDetails.floorCovering.softwood
            ms?.tile!!.isChecked = prop.roomDetails.floorCovering.tile
            ms?.other!!.isChecked = prop.roomDetails.floorCovering.other

            ms?.breakfast!!.isChecked = prop.roomDetails.rooms.breakfast
            ms?.dinning!!.isChecked = prop.roomDetails.rooms.dinning
            ms?.family!!.isChecked = prop.roomDetails.rooms.family
            ms?.laundry!!.isChecked = prop.roomDetails.rooms.laundry
            ms?.library!!.isChecked = prop.roomDetails.rooms.library
            ms?.master_bath!!.isChecked = prop.roomDetails.rooms.masterBath
            ms?.mud!!.isChecked = prop.roomDetails.rooms.mud
            ms?.office!!.isChecked = prop.roomDetails.rooms.office
            ms?.pantry!!.isChecked = prop.roomDetails.rooms.pantry
            ms?.recreation!!.isChecked = prop.roomDetails.rooms.recreation
            ms?.workshop!!.isChecked = prop.roomDetails.rooms.workshop
            ms?.solarium!!.isChecked = prop.roomDetails.rooms.solarium
            ms?.sun!!.isChecked = prop.roomDetails.rooms.sun
            ms?.walk_in_closet!!.isChecked = prop.roomDetails.rooms.walkInCloset
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
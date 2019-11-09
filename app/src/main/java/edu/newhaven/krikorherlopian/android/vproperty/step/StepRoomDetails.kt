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
import kotlinx.android.synthetic.main.appliances.view.*
import kotlinx.android.synthetic.main.floor_covering.view.*
import kotlinx.android.synthetic.main.fragment_step_room_details.view.*
import kotlinx.android.synthetic.main.room_details.view.*


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

            ms?.appliancesdetails?.dishwasher!!.isChecked = prop.roomDetails.appliances.dishWasher
            ms?.appliancesdetails?.washer!!.isChecked = prop.roomDetails.appliances.washer
            ms?.appliancesdetails?.garbage_disposal!!.isChecked =
                prop.roomDetails.appliances.disposal
            ms?.appliancesdetails?.dryer!!.isChecked = prop.roomDetails.appliances.dryer
            ms?.appliancesdetails?.freezer!!.isChecked = prop.roomDetails.appliances.freezer
            ms?.appliancesdetails?.refrigerator!!.isChecked =
                prop.roomDetails.appliances.refrigerator
            ms?.appliancesdetails?.microwave!!.isChecked = prop.roomDetails.appliances.microwave
            ms?.appliancesdetails?.range_oven!!.isChecked = prop.roomDetails.appliances.rangeoven
            ms?.appliancesdetails?.trash_compactor!!.isChecked =
                prop.roomDetails.appliances.trashCompactor

            ms?.floorcoveringlayout?.carpet!!.isChecked = prop.roomDetails.floorCovering.carpet
            ms?.floorcoveringlayout?.concrete!!.isChecked = prop.roomDetails.floorCovering.concrete
            ms?.floorcoveringlayout?.hardwood!!.isChecked = prop.roomDetails.floorCovering.hardwood
            ms?.floorcoveringlayout?.laminate!!.isChecked = prop.roomDetails.floorCovering.laminate
            ms?.floorcoveringlayout?.linoleum!!.isChecked = prop.roomDetails.floorCovering.linoleum
            ms?.floorcoveringlayout?.slate!!.isChecked = prop.roomDetails.floorCovering.slate
            ms?.floorcoveringlayout?.softwood!!.isChecked = prop.roomDetails.floorCovering.softwood
            ms?.floorcoveringlayout?.tile!!.isChecked = prop.roomDetails.floorCovering.tile
            ms?.floorcoveringlayout?.other!!.isChecked = prop.roomDetails.floorCovering.other

            ms?.roomdetailslayout?.breakfast!!.isChecked = prop.roomDetails.rooms.breakfast
            ms?.roomdetailslayout?.dinning!!.isChecked = prop.roomDetails.rooms.dinning
            ms?.roomdetailslayout?.family!!.isChecked = prop.roomDetails.rooms.family
            ms?.roomdetailslayout?.laundry!!.isChecked = prop.roomDetails.rooms.laundry
            ms?.roomdetailslayout?.library!!.isChecked = prop.roomDetails.rooms.library
            ms?.roomdetailslayout?.master_bath!!.isChecked = prop.roomDetails.rooms.masterBath
            ms?.roomdetailslayout?.mud!!.isChecked = prop.roomDetails.rooms.mud
            ms?.roomdetailslayout?.office!!.isChecked = prop.roomDetails.rooms.office
            ms?.roomdetailslayout?.pantry!!.isChecked = prop.roomDetails.rooms.pantry
            ms?.roomdetailslayout?.recreation!!.isChecked = prop.roomDetails.rooms.recreation
            ms?.roomdetailslayout?.workshop!!.isChecked = prop.roomDetails.rooms.workshop
            ms?.roomdetailslayout?.solarium!!.isChecked = prop.roomDetails.rooms.solarium
            ms?.roomdetailslayout?.sun!!.isChecked = prop.roomDetails.rooms.sun
            ms?.roomdetailslayout?.walk_in_closet!!.isChecked = prop.roomDetails.rooms.walkInCloset
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
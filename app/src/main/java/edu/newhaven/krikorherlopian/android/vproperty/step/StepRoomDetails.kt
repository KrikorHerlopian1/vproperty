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
        val layoutView =
            LayoutInflater.from(context).inflate(R.layout.fragment_step_room_details, this, true)
        try {
            onNavigationBarListener = listener
            view = layoutView
            view?.appliancesdetails?.dishwasher!!.isChecked = prop.roomDetails.appliances.dishWasher
            view?.appliancesdetails?.washer!!.isChecked = prop.roomDetails.appliances.washer
            view?.appliancesdetails?.garbage_disposal!!.isChecked =
                prop.roomDetails.appliances.disposal
            view?.appliancesdetails?.dryer!!.isChecked = prop.roomDetails.appliances.dryer
            view?.appliancesdetails?.freezer!!.isChecked = prop.roomDetails.appliances.freezer
            view?.appliancesdetails?.refrigerator!!.isChecked =
                prop.roomDetails.appliances.refrigerator
            view?.appliancesdetails?.microwave!!.isChecked = prop.roomDetails.appliances.microwave
            view?.appliancesdetails?.range_oven!!.isChecked = prop.roomDetails.appliances.rangeoven
            view?.appliancesdetails?.trash_compactor!!.isChecked =
                prop.roomDetails.appliances.trashCompactor

            view?.floorcoveringlayout?.carpet!!.isChecked = prop.roomDetails.floorCovering.carpet
            view?.floorcoveringlayout?.concrete!!.isChecked =
                prop.roomDetails.floorCovering.concrete
            view?.floorcoveringlayout?.hardwood!!.isChecked =
                prop.roomDetails.floorCovering.hardwood
            view?.floorcoveringlayout?.laminate!!.isChecked =
                prop.roomDetails.floorCovering.laminate
            view?.floorcoveringlayout?.linoleum!!.isChecked =
                prop.roomDetails.floorCovering.linoleum
            view?.floorcoveringlayout?.slate!!.isChecked = prop.roomDetails.floorCovering.slate
            view?.floorcoveringlayout?.softwood!!.isChecked =
                prop.roomDetails.floorCovering.softwood
            view?.floorcoveringlayout?.tile!!.isChecked = prop.roomDetails.floorCovering.tile
            view?.floorcoveringlayout?.other!!.isChecked = prop.roomDetails.floorCovering.other

            view?.roomdetailslayout?.breakfast!!.isChecked = prop.roomDetails.rooms.breakfast
            view?.roomdetailslayout?.dinning!!.isChecked = prop.roomDetails.rooms.dinning
            view?.roomdetailslayout?.family!!.isChecked = prop.roomDetails.rooms.family
            view?.roomdetailslayout?.laundry!!.isChecked = prop.roomDetails.rooms.laundry
            view?.roomdetailslayout?.library!!.isChecked = prop.roomDetails.rooms.library
            view?.roomdetailslayout?.master_bath!!.isChecked = prop.roomDetails.rooms.masterBath
            view?.roomdetailslayout?.mud!!.isChecked = prop.roomDetails.rooms.mud
            view?.roomdetailslayout?.office!!.isChecked = prop.roomDetails.rooms.office
            view?.roomdetailslayout?.pantry!!.isChecked = prop.roomDetails.rooms.pantry
            view?.roomdetailslayout?.recreation!!.isChecked = prop.roomDetails.rooms.recreation
            view?.roomdetailslayout?.workshop!!.isChecked = prop.roomDetails.rooms.workshop
            view?.roomdetailslayout?.solarium!!.isChecked = prop.roomDetails.rooms.solarium
            view?.roomdetailslayout?.sun!!.isChecked = prop.roomDetails.rooms.sun
            view?.roomdetailslayout?.walk_in_closet!!.isChecked =
                prop.roomDetails.rooms.walkInCloset
        } catch (e: Exception) {
        }

    }

    override fun verifyStep(): VerificationError? {
        try {
            var appliances: Appliances = Appliances(
                view?.dishwasher!!.isChecked, view?.dryer!!.isChecked,
                view?.freezer!!.isChecked, view?.garbage_disposal!!.isChecked,
                view?.microwave!!.isChecked, view?.range_oven!!.isChecked,
                view?.refrigerator!!.isChecked, view?.washer!!.isChecked,
                view?.trash_compactor!!.isChecked
            )
            var floorCovering: FloorCovering = FloorCovering(
                view?.carpet!!.isChecked, view?.concrete!!.isChecked,
                view?.hardwood!!.isChecked, view?.laminate!!.isChecked,
                view?.slate!!.isChecked, view?.softwood!!.isChecked,
                view?.linoleum!!.isChecked, view?.tile!!.isChecked,
                view?.other!!.isChecked
            )
            var room: Room = Room(
                view?.breakfast!!.isChecked, view?.dinning!!.isChecked,
                view?.family!!.isChecked, view?.laundry!!.isChecked,
                view?.library!!.isChecked, view?.master_bath!!.isChecked,
                view?.mud!!.isChecked, view?.office!!.isChecked,
                view?.pantry!!.isChecked, view?.recreation!!.isChecked,
                view?.workshop!!.isChecked, view?.solarium!!.isChecked,
                view?.sun!!.isChecked, view?.walk_in_closet!!.isChecked
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
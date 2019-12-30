package edu.newhaven.krikorherlopian.android.vproperty.step


import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.font
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_step_home_facts.view.*



class StepHomeFacts(context: Context, listener: OnNavigationBarListener, var property: Property) :
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
            LayoutInflater.from(context).inflate(R.layout.fragment_step_home_facts, this, true)
        try {
            onNavigationBarListener = listener
            view = layoutView
            setUpFonts()
            layoutView.priceInput.setText("" + property.homeFacts.price)
            if (property.homeFacts.isSale) {
                forsale.isChecked = true
                priceLayout.hint = resources.getString(R.string.price)
            }
            if (property.homeFacts.isRent) {
                forrent.isChecked = true
                priceLayout.hint = resources.getString(R.string.price_per_month)
            }
            if (!property.homeFacts.bedrooms.trim().equals("")) {
                bed_number_picker.value = property.homeFacts.bedrooms.toInt()
            }

            if (!property.homeFacts.bathrooms?.trim().equals("")) {
                bath_number_picker.value = property.homeFacts.bathrooms?.toInt()!!
            }

            if (!property.homeFacts.totalRooms?.trim().equals("")) {
                rooms_number_picker.value = property.homeFacts.totalRooms?.toInt()!!
            }

            if (!property.homeFacts.parkingSpaces?.trim().equals("")) {
                parking_number_picker.value = property.homeFacts.parkingSpaces?.toInt()!!
            }

            yearbuiltinput.setText(property.homeFacts.yearBuilt)
            hoainput.setText(property.homeFacts.hoadues)
            modal_year_input.setText(property.homeFacts.structuralModalYear)
            floor_number_input.setText(property.homeFacts.floorNumber)
            finished_square_feet_input.setText(property.homeFacts.finishedSqFt)
            garage_ft_input.setText(property.homeFacts.garageSqFt)
            basement_sq_ft_input.setText(property.homeFacts.basementSqFt)
            lot_size_input.setText(property.homeFacts.lotSizeFqFt)


            forsale.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    forrent.isChecked = false
                    priceLayout.hint = resources.getString(R.string.price)
                }
            }


            forrent.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    forsale.isChecked = false
                    priceLayout.hint = resources.getString(R.string.price_per_month)
                } else
                    priceLayout.hint = resources.getString(R.string.price)
            }


        } catch (e: Exception) {
            Log.d("step frag", "Exception")
        }

    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(context.assets, "" + font)
        view?.priceLayout?.typeface = tf
        view?.priceInput?.typeface = tf
        forrent.typeface = tf
        forsale.typeface = tf
        view?.yearbuiltinput?.typeface = tf
        view?.yearbuilt?.typeface = tf
        view?.hoainput?.typeface = tf
        view?.hoalayout?.typeface = tf
        view?.modal_year_input?.typeface = tf
        view?.modal_year?.typeface = tf
        view?.floor_number_input?.typeface = tf
        view?.floor_number?.typeface = tf
        view?.finished_square_feet_input?.typeface = tf
        view?.finished_square_feet?.typeface = tf
        view?.garage_ft?.typeface = tf
        view?.garage_ft_input?.typeface = tf
        view?.basement_sq_ft?.typeface = tf
        view?.basement_sq_ft_input?.typeface = tf
        view?.lot_size?.typeface = tf
        view?.lot_size_input?.typeface = tf
    }

    override fun verifyStep(): VerificationError? {
        try {
            var state = 0
            resetForms()
            if (view?.priceInput?.text.isNullOrBlank()) {
                state = 1
                view?.priceLayout?.error = resources.getString(R.string.enter_price)
            }
            if (state == 1) {
                return VerificationError(resources.getString(R.string.enter_required_fields))
            } else if (view?.forrent?.isChecked == false && view?.forsale?.isChecked == false) {
                return VerificationError(resources.getString(R.string.choose_sale_rent))
            } else {
                onNavigationBarListener?.addHomeFacts(
                    view?.priceInput?.text.toString(), forrent.isChecked, forsale.isChecked,
                    "" + bed_number_picker.value, "" + bath_number_picker.value,
                    "" + rooms_number_picker.value, "" + parking_number_picker.value,
                    yearbuiltinput?.text.toString(), hoainput?.text.toString(),
                    modal_year_input?.text.toString(), floor_number_input?.text.toString(),
                    finished_square_feet_input?.text.toString(), lot_size_input?.text.toString(),
                    basement_sq_ft_input?.text.toString(), garage_ft_input?.text.toString()
                )
            }

        } catch (e: Exception) {
            Log.d("step frag", "Exception")
        }

        return null
    }

    private fun resetForms() {
        view?.priceLayout?.error = null
        view?.priceLayout?.isErrorEnabled = false
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
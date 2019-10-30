package edu.newhaven.krikorherlopian.android.vproperty.step


import android.content.Context
import android.graphics.Typeface
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
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_step_home_facts.view.*


class StepHomeFacts(context: Context, listener: OnNavigationBarListener) : FrameLayout(context),
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
        val v = LayoutInflater.from(context).inflate(R.layout.fragment_step_home_facts, this, true)
        try {
            onNavigationBarListener = listener
            ms = v
            setUpFonts()
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
        }

    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(context.assets, "" + font)
        ms?.priceLayout?.typeface = tf
        ms?.priceInput?.typeface = tf
        forrent.typeface = tf
        forsale.typeface = tf
        ms?.yearbuiltinput?.typeface = tf
        ms?.yearbuilt?.typeface = tf
        ms?.hoainput?.typeface = tf
        ms?.hoalayout?.typeface = tf
        ms?.modal_year_input?.typeface = tf
        ms?.modal_year?.typeface = tf
        ms?.floor_number_input?.typeface = tf
        ms?.floor_number?.typeface = tf
        ms?.finished_square_feet_input?.typeface = tf
        ms?.finished_square_feet?.typeface = tf
        ms?.garage_ft?.typeface = tf
        ms?.garage_ft_input?.typeface = tf
        ms?.basement_sq_ft?.typeface = tf
        ms?.basement_sq_ft_input?.typeface = tf
        ms?.lot_size?.typeface = tf
        ms?.lot_size_input?.typeface = tf
    }

    override fun verifyStep(): VerificationError? {
        try {
            var state = 0
            resetForms()
            if (ms?.priceInput?.text.isNullOrBlank()) {
                state = 1
                ms?.priceLayout?.error = resources.getString(R.string.enter_price)
            }
            if (state == 1) {
                return VerificationError(resources.getString(R.string.enter_required_fields))
            } else if (ms?.forrent?.isChecked == false && ms?.forsale?.isChecked == false) {
                return VerificationError(resources.getString(R.string.choose_sale_rent))
            } else {
                onNavigationBarListener?.addHomeFacts(
                    ms?.priceInput?.text.toString(), forrent.isChecked, forsale.isChecked,
                    "" + bed_number_picker.value, "" + bath_number_picker.value,
                    "" + rooms_number_picker.value, "" + parking_number_picker.value,
                    yearbuiltinput?.text.toString(), hoainput?.text.toString(),
                    modal_year_input?.text.toString(), floor_number_input?.text.toString(),
                    finished_square_feet_input?.text.toString(), lot_size_input?.text.toString(),
                    basement_sq_ft_input?.text.toString(), garage_ft_input?.text.toString()
                )
            }

        } catch (e: Exception) {
        }

        return null
    }

    private fun resetForms() {
        ms?.priceLayout?.error = null
        ms?.priceLayout?.isErrorEnabled = false
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
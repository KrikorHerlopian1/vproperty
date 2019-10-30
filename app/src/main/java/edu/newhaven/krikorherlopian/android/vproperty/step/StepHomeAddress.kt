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
import kotlinx.android.synthetic.main.fragment_step_home_address.view.*

class StepHomeAddress(context: Context, listener: OnNavigationBarListener) : FrameLayout(context),
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
            LayoutInflater.from(context).inflate(R.layout.fragment_step_home_address, this, true)
        try {
            onNavigationBarListener = listener
            ms = v
            setUpFonts()
        } catch (e: Exception) {
        }

    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(context.assets, "" + font)
        ms?.houseNameInputLayout?.typeface = tf
        ms?.houseName?.typeface = tf
        ms?.addressName?.typeface = tf
        ms?.zipCodeInput?.typeface = tf
        ms?.longitudeInput?.typeface = tf
        ms?.latitudeInput?.typeface = tf
        ms?.descriptionLayout?.typeface = tf
        ms?.addressInputLayout?.typeface = tf
        ms?.longitudeLayout?.typeface = tf
        ms?.latitudeLayout?.typeface = tf
        ms?.zipCodeLayout?.typeface = tf
        ms?.descriptionInputLayout?.typeface = tf
        ms?.descriptionInputLayout?.typeface = tf
    }

    override fun verifyStep(): VerificationError? {
        try {
            var state = 0
            resetForms()
            if (ms?.houseName?.text.isNullOrBlank()) {
                state = 1
                ms?.houseNameInputLayout?.error = resources.getString(R.string.choose_house_name)
            }
            if (ms?.addressName?.text.isNullOrBlank()) {
                state = 1
                ms?.addressInputLayout?.error = resources.getString(R.string.enter_home_address)
            }
            if (ms?.zipCodeInput?.text.isNullOrBlank()) {
                state = 1
                ms?.zipCodeLayout?.error = resources.getString(R.string.enter_zip_code)
            }
            if (ms?.longitudeInput?.text.isNullOrBlank()) {
                state = 1
                ms?.longitudeLayout?.error = resources.getString(R.string.enter_longitude)
            }
            if (ms?.latitudeInput?.text.isNullOrBlank()) {
                state = 1
                ms?.latitudeLayout?.error = resources.getString(R.string.enter_latitude)
            }
            if (state == 1) {
                return VerificationError(resources.getString(R.string.enter_required_fields))
            } else
                onNavigationBarListener?.addAddress(
                    ms?.houseName?.text?.trim().toString(),
                    ms?.addressName?.text?.trim().toString(),
                    ms?.zipCodeInput?.text?.trim().toString(),
                    ms?.longitudeInput?.text?.trim().toString()
                    ,
                    ms?.latitudeInput?.text?.trim().toString(),
                    ms?.descriptionLayout?.text?.trim().toString()
                )

        } catch (e: Exception) {
        }

        return null
    }

    private fun resetForms() {
        ms?.houseNameInputLayout?.error = null
        ms?.houseNameInputLayout?.isErrorEnabled = false
        ms?.addressInputLayout?.error = null
        ms?.addressInputLayout?.isErrorEnabled = false
        ms?.zipCodeLayout?.error = null
        ms?.zipCodeLayout?.isErrorEnabled = false
        ms?.longitudeLayout?.error = null
        ms?.longitudeLayout?.isErrorEnabled = false
        ms?.latitudeLayout?.error = null
        ms?.latitudeLayout?.isErrorEnabled = false
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
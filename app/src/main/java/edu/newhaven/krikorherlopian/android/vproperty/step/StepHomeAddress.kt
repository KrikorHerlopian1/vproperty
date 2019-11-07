package edu.newhaven.krikorherlopian.android.vproperty.step

import android.content.Context
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.font
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_step_home_address.view.*
import java.util.*

class StepHomeAddress(context: Context, listener: OnNavigationBarListener, var property: Property) :
    FrameLayout(context),
    Step {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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
            v.houseName.setText(property.houseName.toString())
            v.addressName.setText(property.address.addressName.toString())
            v.zipCodeInput.setText(property.address.zipCode.toString())
            v.longitudeInput.setText(property.address.longitude.toString())
            v.latitudeInput.setText(property.address.latitude.toString())
            v.descriptionLayout.setText(property.address.descriptionAddress.toString())
            setUpFonts()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (property.address.latitude.equals("") && property.address.longitude.equals(""))
                getLocation()
        } catch (e: Exception) {
        }
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    try {
                        val address = ""
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val cityName = addresses[0].getAddressLine(0)
                        val stateName = addresses[0].getAddressLine(1)
                        val countryName = addresses[0].getAddressLine(2)
                        var add: String = ""
                        ms?.longitudeInput?.setText(location.longitude.toDouble().toString())
                        ms?.latitudeInput?.setText(location.latitude.toDouble().toString())
                        if (address == null || address.trim().equals("")) {
                            if (cityName != null) {
                                add = cityName
                                if (stateName != null) {
                                    add = add + " ," + stateName
                                }
                                addressName.setText(add)
                            } else if (stateName != null) {
                                add = stateName
                                addressName.setText(add)
                            }
                        }
                    } catch (e: Exception) {
                    }

                }
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
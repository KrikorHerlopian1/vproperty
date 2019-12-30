package edu.newhaven.krikorherlopian.android.vproperty.step

import android.content.Context
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.util.Log
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
            LayoutInflater.from(context).inflate(R.layout.fragment_step_home_address, this, true)
        try {
            onNavigationBarListener = listener
            view = layoutView
            layoutView.houseName.setText(property.houseName.toString())
            layoutView.addressName.setText(property.address.addressName.toString())
            layoutView.zipCodeInput.setText(property.address.zipCode.toString())
            layoutView.longitudeInput.setText(property.address.longitude.toString())
            layoutView.latitudeInput.setText(property.address.latitude.toString())
            layoutView.descriptionLayout.setText(property.address.descriptionAddress.toString())
            setUpFonts()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (property.address.latitude.equals("") && property.address.longitude.equals(""))
                getLocation()
        } catch (e: Exception) {
            Log.d("step frag", "Exception")
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
                        view?.longitudeInput?.setText(location.longitude.toDouble().toString())
                        view?.latitudeInput?.setText(location.latitude.toDouble().toString())
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
                        Log.d("step frag", "Exception")
                    }

                }
            }
    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(context.assets, "" + font)
        view?.houseNameInputLayout?.typeface = tf
        view?.houseName?.typeface = tf
        view?.addressName?.typeface = tf
        view?.zipCodeInput?.typeface = tf
        view?.longitudeInput?.typeface = tf
        view?.latitudeInput?.typeface = tf
        view?.descriptionLayout?.typeface = tf
        view?.addressInputLayout?.typeface = tf
        view?.longitudeLayout?.typeface = tf
        view?.latitudeLayout?.typeface = tf
        view?.zipCodeLayout?.typeface = tf
        view?.descriptionInputLayout?.typeface = tf
        view?.descriptionInputLayout?.typeface = tf
    }

    override fun verifyStep(): VerificationError? {
        try {
            var state = 0
            resetForview()
            if (view?.houseName?.text.isNullOrBlank()) {
                state = 1
                view?.houseNameInputLayout?.error = resources.getString(R.string.choose_house_name)
            }
            if (view?.addressName?.text.isNullOrBlank()) {
                state = 1
                view?.addressInputLayout?.error = resources.getString(R.string.enter_home_address)
            }
            if (view?.zipCodeInput?.text.isNullOrBlank()) {
                state = 1
                view?.zipCodeLayout?.error = resources.getString(R.string.enter_zip_code)
            }
            if (view?.longitudeInput?.text.isNullOrBlank()) {
                state = 1
                view?.longitudeLayout?.error = resources.getString(R.string.enter_longitude)
            }
            if (view?.latitudeInput?.text.isNullOrBlank()) {
                state = 1
                view?.latitudeLayout?.error = resources.getString(R.string.enter_latitude)
            }
            if (state == 1) {
                return VerificationError(resources.getString(R.string.enter_required_fields))
            } else
                onNavigationBarListener?.addAddress(
                    view?.houseName?.text?.trim().toString(),
                    view?.addressName?.text?.trim().toString(),
                    view?.zipCodeInput?.text?.trim().toString(),
                    view?.longitudeInput?.text?.trim().toString()
                    ,
                    view?.latitudeInput?.text?.trim().toString(),
                    view?.descriptionLayout?.text?.trim().toString()
                )

        } catch (e: Exception) {
            Log.d("step frag", "Exception")
        }

        return null
    }

    private fun resetForview() {
        view?.houseNameInputLayout?.error = null
        view?.houseNameInputLayout?.isErrorEnabled = false
        view?.addressInputLayout?.error = null
        view?.addressInputLayout?.isErrorEnabled = false
        view?.zipCodeLayout?.error = null
        view?.zipCodeLayout?.isErrorEnabled = false
        view?.longitudeLayout?.error = null
        view?.longitudeLayout?.isErrorEnabled = false
        view?.latitudeLayout?.error = null
        view?.latitudeLayout?.isErrorEnabled = false
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
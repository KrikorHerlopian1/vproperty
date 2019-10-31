package edu.newhaven.krikorherlopian.android.vproperty.step

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.font
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.loggedInUser
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import edu.newhaven.krikorherlopian.android.vproperty.model.User
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_step_more_info.view.*


class StepMoreInformation(context: Context, listener: OnNavigationBarListener, var prop: Property) :
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
        val v = LayoutInflater.from(context).inflate(R.layout.fragment_step_more_info, this, true)
        try {
            onNavigationBarListener = listener
            ms = v
            setUpFonts()
            ms?.picture!!.setOnClickListener {
                onNavigationBarListener?.addPictureClicked()
            }

            if (!prop.photoUrl.trim().equals("")) {
                Glide.with(context).load(prop.photoUrl)
                    .placeholder(R.drawable.placeholderdetail)
                    .into(
                        ms?.picture!!
                    )
            } else {
                Glide.with(context).load(R.drawable.placeholderdetail)
                    .placeholder(R.drawable.placeholderdetail)
                    .into(
                        ms?.picture!!
                    )
            }

            if (prop.id.trim().equals("")) {
                val db = FirebaseFirestore.getInstance()
                try {
                    val docRef =
                        db.collection("users").document(loggedInUser?.email?.toString()!!).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    var user = document.toObject(User::class.java)
                                    ms?.contactinput?.setText(user?.phoneNumber)
                                } else {
                                }
                            }
                            .addOnFailureListener { exception ->
                            }
                } catch (e: Exception) {
                }
            }
            ms?.relatedWebsite?.setText(prop.relatedWebsite)
            ms?.virtualtourinput?.setText(prop.virtualTour)
            ms?.contactinput?.setText(prop.contactPhone)
        } catch (e: Exception) {
        }

    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(context.assets, "" + font)
    }

    override fun verifyStep(): VerificationError? {
        try {
            val bitmap = (ms?.picture?.drawable as BitmapDrawable).bitmap
            onNavigationBarListener?.finishStep(
                ms?.relatedWebsite?.text.toString(),
                ms?.virtualtourinput?.text.toString(),
                ms?.contactinput?.text.toString(),
                bitmap
            )
        } catch (e: Exception) {
        }

        return null
    }

    private fun resetForms() {
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
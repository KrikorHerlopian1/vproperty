package edu.newhaven.krikorherlopian.android.vproperty.interfaces

import android.widget.ImageView
import edu.newhaven.krikorherlopian.android.vproperty.model.Property

interface FragmentActivityCommunication {
    fun startActivityDet(property: Property)
    fun startActivityDetWithTransition(property: Property, imageView: ImageView)
    fun addProfileButtonClicked()
    fun updateProfile()
}
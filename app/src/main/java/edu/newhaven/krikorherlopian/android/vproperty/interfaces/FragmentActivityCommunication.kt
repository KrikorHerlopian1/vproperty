package edu.newhaven.krikorherlopian.android.vproperty.interfaces

import android.widget.ImageView
import edu.newhaven.krikorherlopian.android.vproperty.model.Property

interface FragmentActivityCommunication {
    fun startActivityDet(image: ImageView, property: Property)
    fun addProfileButtonClicked()
    fun updateProfile()
}
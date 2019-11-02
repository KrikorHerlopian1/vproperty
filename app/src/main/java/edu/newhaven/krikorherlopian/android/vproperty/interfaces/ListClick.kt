package edu.newhaven.krikorherlopian.android.vproperty.interfaces

import android.widget.ImageView

interface ListClick {
    fun rowClicked(position: Int, position2: Int = 0, imageLayout: ImageView? = null)
    fun deleteRow(position: Int)
}
package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class Parking(
    var carport: Boolean = false, var onStreet: Boolean = false,
    var offStreet: Boolean = false, var garageAttached: Boolean = false,
    var garageDetached: Boolean = false, var none: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()

        if (carport) {
            list.add(ItemValuePair(context.resources.getString(R.string.carport), "", 0))
        }
        if (garageAttached) {
            list.add(ItemValuePair(context.resources.getString(R.string.garage_attached), "", 0))
        }
        if (garageDetached) {
            list.add(ItemValuePair(context.resources.getString(R.string.garage_detached), "", 0))
        }
        if (offStreet) {
            list.add(ItemValuePair(context.resources.getString(R.string.off_street), "", 0))
        }
        if (onStreet) {
            list.add(ItemValuePair(context.resources.getString(R.string.on_street), "", 0))
        }
        if (none) {
            list.add(ItemValuePair(context.resources.getString(R.string.none), "", 0))
        }
        return list
    }
}
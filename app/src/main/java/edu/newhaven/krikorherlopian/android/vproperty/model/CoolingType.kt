package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class CoolingType(
    var central: Boolean = false, var evaporative: Boolean = false,
    var geoThermal: Boolean = false, var refrigeration: Boolean = false,
    var solar: Boolean = false, var wall: Boolean = false,
    var other: Boolean = false, var none: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()

        if (central) {
            list.add(ItemValuePair(context.resources.getString(R.string.central), "", 0))
        }
        if (evaporative) {
            list.add(ItemValuePair(context.resources.getString(R.string.evaporative), "", 0))
        }
        if (geoThermal) {
            list.add(ItemValuePair(context.resources.getString(R.string.geothermal), "", 0))
        }
        if (refrigeration) {
            list.add(ItemValuePair(context.resources.getString(R.string.refrigeration), "", 0))
        }
        if (solar) {
            list.add(ItemValuePair(context.resources.getString(R.string.solar), "", 0))
        }
        if (wall) {
            list.add(ItemValuePair(context.resources.getString(R.string.wall), "", 0))
        }
        if (none) {
            list.add(ItemValuePair(context.resources.getString(R.string.none), "", 0))
        }
        if (other) {
            list.add(ItemValuePair(context.resources.getString(R.string.other), "", 0))
        }

        return list
    }
}
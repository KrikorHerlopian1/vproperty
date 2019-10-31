package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class HeatingType(
    var baseboard: Boolean = false, var forcedAir: Boolean = false,
    var geoThermal: Boolean = false, var heatPump: Boolean = false,
    var radiant: Boolean = false, var stove: Boolean = false,
    var wall: Boolean = false, var other: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (baseboard) {
            list.add(ItemValuePair(context.resources.getString(R.string.baseboard), "", 0))
        }
        if (forcedAir) {
            list.add(ItemValuePair(context.resources.getString(R.string.forced_air), "", 0))
        }
        if (geoThermal) {
            list.add(ItemValuePair(context.resources.getString(R.string.geothermal), "", 0))
        }
        if (heatPump) {
            list.add(ItemValuePair(context.resources.getString(R.string.heat_pump), "", 0))
        }
        if (radiant) {
            list.add(ItemValuePair(context.resources.getString(R.string.radiant), "", 0))
        }
        if (stove) {
            list.add(ItemValuePair(context.resources.getString(R.string.stove), "", 0))
        }
        if (wall) {
            list.add(ItemValuePair(context.resources.getString(R.string.wall), "", 0))
        }
        if (other) {
            list.add(ItemValuePair(context.resources.getString(R.string.other), "", 0))
        }
        return list
    }
}
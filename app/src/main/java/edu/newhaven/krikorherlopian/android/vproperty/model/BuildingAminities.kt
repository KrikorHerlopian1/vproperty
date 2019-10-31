package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class BuildingAminities(
    var basketballCourt: Boolean = false, var disabledAccess: Boolean = false,
    var doorman: Boolean = false, var elevator: Boolean = false,
    var fitnessCenter: Boolean = false, var sportsCourt: Boolean = false,
    var storage: Boolean = false, var tennisCourt: Boolean = false,
    var nearTransportation: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()

        if (basketballCourt) {
            list.add(ItemValuePair(context.resources.getString(R.string.basketball_court), "", 0))
        }
        if (disabledAccess) {
            list.add(ItemValuePair(context.resources.getString(R.string.disabled_access), "", 0))
        }
        if (doorman) {
            list.add(ItemValuePair(context.resources.getString(R.string.doorman), "", 0))
        }
        if (elevator) {
            list.add(ItemValuePair(context.resources.getString(R.string.elevator), "", 0))
        }
        if (fitnessCenter) {
            list.add(ItemValuePair(context.resources.getString(R.string.fitness_center), "", 0))
        }
        if (nearTransportation) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.near_transportation),
                    "",
                    0
                )
            )
        }
        if (sportsCourt) {
            list.add(ItemValuePair(context.resources.getString(R.string.sports_court), "", 0))
        }
        if (storage) {
            list.add(ItemValuePair(context.resources.getString(R.string.storage), "", 0))
        }
        if (tennisCourt) {
            list.add(ItemValuePair(context.resources.getString(R.string.tennis_court), "", 0))
        }

        return list
    }
}
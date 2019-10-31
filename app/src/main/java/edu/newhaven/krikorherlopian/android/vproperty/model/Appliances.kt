package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable


data class Appliances(
    var dishWasher: Boolean = false, var dryer: Boolean = false,
    var freezer: Boolean = false, var disposal: Boolean = false,
    var microwave: Boolean = false, var rangeoven: Boolean = false,
    var refrigerator: Boolean = false, var washer: Boolean = false,
    var trashCompactor: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (dishWasher) {
            list.add(ItemValuePair(context.resources.getString(R.string.dishwasher), "", 0))
        }
        if (washer) {
            list.add(ItemValuePair(context.resources.getString(R.string.washer), "", 0))
        }
        if (disposal) {
            list.add(ItemValuePair(context.resources.getString(R.string.garbage_disposal), "", 0))
        }
        if (dryer) {
            list.add(ItemValuePair(context.resources.getString(R.string.dryer), "", 0))
        }
        if (freezer) {
            list.add(ItemValuePair(context.resources.getString(R.string.freezer), "", 0))
        }
        if (refrigerator) {
            list.add(ItemValuePair(context.resources.getString(R.string.refrigerator), "", 0))
        }
        if (microwave) {
            list.add(ItemValuePair(context.resources.getString(R.string.microwave), "", 0))
        }
        if (rangeoven) {
            list.add(ItemValuePair(context.resources.getString(R.string.range_oven), "", 0))
        }
        if (trashCompactor) {
            list.add(ItemValuePair(context.resources.getString(R.string.trash_compactor), "", 0))
        }


        return list
    }
}
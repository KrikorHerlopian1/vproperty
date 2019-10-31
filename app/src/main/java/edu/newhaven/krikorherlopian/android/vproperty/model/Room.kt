package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class Room(
    var breakfast: Boolean = false, var dinning: Boolean = false,
    var family: Boolean = false, var laundry: Boolean = false,
    var library: Boolean = false, var masterBath: Boolean = false,
    var mud: Boolean = false, var office: Boolean = false,
    var pantry: Boolean = false, var recreation: Boolean = false,
    var workshop: Boolean = false, var solarium: Boolean = false,
    var sun: Boolean = false, var walkInCloset: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (breakfast) {
            list.add(ItemValuePair(context.resources.getString(R.string.breakfast), "", 0))
        }
        if (dinning) {
            list.add(ItemValuePair(context.resources.getString(R.string.dinning), "", 0))
        }
        if (family) {
            list.add(ItemValuePair(context.resources.getString(R.string.family), "", 0))
        }
        if (laundry) {
            list.add(ItemValuePair(context.resources.getString(R.string.laundry), "", 0))
        }
        if (library) {
            list.add(ItemValuePair(context.resources.getString(R.string.library), "", 0))
        }
        if (masterBath) {
            list.add(ItemValuePair(context.resources.getString(R.string.master_bath), "", 0))
        }
        if (mud) {
            list.add(ItemValuePair(context.resources.getString(R.string.mud), "", 0))
        }
        if (office) {
            list.add(ItemValuePair(context.resources.getString(R.string.office), "", 0))
        }
        if (pantry) {
            list.add(ItemValuePair(context.resources.getString(R.string.pantry), "", 0))
        }
        if (recreation) {
            list.add(ItemValuePair(context.resources.getString(R.string.recreation), "", 0))
        }
        if (solarium) {
            list.add(ItemValuePair(context.resources.getString(R.string.solarium), "", 0))
        }
        if (sun) {
            list.add(ItemValuePair(context.resources.getString(R.string.sun), "", 0))
        }
        if (walkInCloset) {
            list.add(ItemValuePair(context.resources.getString(R.string.walk_in_closet), "", 0))
        }
        if (workshop) {
            list.add(ItemValuePair(context.resources.getString(R.string.workshop), "", 0))
        }

        return list
    }
}
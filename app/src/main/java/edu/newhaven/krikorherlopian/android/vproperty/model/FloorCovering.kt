package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable


data class FloorCovering(
    var carpet: Boolean = false, var concrete: Boolean = false,
    var hardwood: Boolean = false, var laminate: Boolean = false,
    var slate: Boolean = false, var softwood: Boolean = false,
    var linoleum: Boolean = false, var tile: Boolean = false,
    var other: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (carpet) {
            list.add(ItemValuePair(context.resources.getString(R.string.carpet), "", 0))
        }
        if (concrete) {
            list.add(ItemValuePair(context.resources.getString(R.string.concrete), "", 0))
        }
        if (hardwood) {
            list.add(ItemValuePair(context.resources.getString(R.string.hardwood), "", 0))
        }
        if (laminate) {
            list.add(ItemValuePair(context.resources.getString(R.string.laminate), "", 0))
        }
        if (linoleum) {
            list.add(ItemValuePair(context.resources.getString(R.string.linoleum), "", 0))
        }
        if (slate) {
            list.add(ItemValuePair(context.resources.getString(R.string.slate), "", 0))
        }
        if (softwood) {
            list.add(ItemValuePair(context.resources.getString(R.string.softwood), "", 0))
        }
        if (tile) {
            list.add(ItemValuePair(context.resources.getString(R.string.tile), "", 0))
        }
        if (other) {
            list.add(ItemValuePair(context.resources.getString(R.string.other), "", 0))
        }

        return list
    }
}
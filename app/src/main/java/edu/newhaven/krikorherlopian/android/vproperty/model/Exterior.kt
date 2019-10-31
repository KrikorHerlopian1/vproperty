package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class Exterior(
    var brick: Boolean = false, var cement: Boolean = false,
    var composition: Boolean = false, var metal: Boolean = false,
    var shingle: Boolean = false, var stone: Boolean = false,
    var stucco: Boolean = false, var vinyl: Boolean = false,
    var wood: Boolean = false, var woodProducts: Boolean = false,
    var other: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()

        if (brick) {
            list.add(ItemValuePair(context.resources.getString(R.string.brick), "", 0))
        }
        if (cement) {
            list.add(ItemValuePair(context.resources.getString(R.string.cement), "", 0))
        }
        if (composition) {
            list.add(ItemValuePair(context.resources.getString(R.string.composition), "", 0))
        }
        if (metal) {
            list.add(ItemValuePair(context.resources.getString(R.string.metal), "", 0))
        }
        if (shingle) {
            list.add(ItemValuePair(context.resources.getString(R.string.shingle), "", 0))
        }
        if (stone) {
            list.add(ItemValuePair(context.resources.getString(R.string.stone), "", 0))
        }
        if (stucco) {
            list.add(ItemValuePair(context.resources.getString(R.string.stucco), "", 0))
        }
        if (vinyl) {
            list.add(ItemValuePair(context.resources.getString(R.string.vinyl), "", 0))
        }
        if (wood) {
            list.add(ItemValuePair(context.resources.getString(R.string.wood), "", 0))
        }
        if (woodProducts) {
            list.add(ItemValuePair(context.resources.getString(R.string.wood_products), "", 0))
        }
        if (other) {
            list.add(ItemValuePair(context.resources.getString(R.string.other), "", 0))
        }

        return list
    }
}
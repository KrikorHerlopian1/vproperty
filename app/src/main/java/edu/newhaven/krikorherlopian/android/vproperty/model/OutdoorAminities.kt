package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class OutdoorAminities(
    var balconyPatio: Boolean = false, var lawn: Boolean = false,
    var barbecueArea: Boolean = false, var pond: Boolean = false,
    var deck: Boolean = false, var pool: Boolean = false,
    var dock: Boolean = false, var porch: Boolean = false,
    var fencedYard: Boolean = false, var rvParking: Boolean = false,
    var garden: Boolean = false, var sauna: Boolean = false,
    var greenHouse: Boolean = false, var sprinkerlSystem: Boolean = false,
    var hotTubSpa: Boolean = false, var waterfront: Boolean = false
) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()

        if (balconyPatio) {
            list.add(ItemValuePair(context.resources.getString(R.string.balcony_patio), "", 0))
        }
        if (barbecueArea) {
            list.add(ItemValuePair(context.resources.getString(R.string.barbecue_area), "", 0))
        }
        if (deck) {
            list.add(ItemValuePair(context.resources.getString(R.string.deck), "", 0))
        }
        if (dock) {
            list.add(ItemValuePair(context.resources.getString(R.string.dock), "", 0))
        }
        if (fencedYard) {
            list.add(ItemValuePair(context.resources.getString(R.string.fenced_yard), "", 0))
        }
        if (garden) {
            list.add(ItemValuePair(context.resources.getString(R.string.garden), "", 0))
        }
        if (greenHouse) {
            list.add(ItemValuePair(context.resources.getString(R.string.greenhouse), "", 0))
        }
        if (hotTubSpa) {
            list.add(ItemValuePair(context.resources.getString(R.string.hottubspa), "", 0))
        }
        if (lawn) {
            list.add(ItemValuePair(context.resources.getString(R.string.lawn), "", 0))
        }
        if (pond) {
            list.add(ItemValuePair(context.resources.getString(R.string.pond), "", 0))
        }
        if (pool) {
            list.add(ItemValuePair(context.resources.getString(R.string.pool), "", 0))
        }
        if (porch) {
            list.add(ItemValuePair(context.resources.getString(R.string.porch), "", 0))
        }
        if (rvParking) {
            list.add(ItemValuePair(context.resources.getString(R.string.rv_parking), "", 0))
        }
        if (sauna) {
            list.add(ItemValuePair(context.resources.getString(R.string.sauna), "", 0))
        }
        if (sprinkerlSystem) {
            list.add(ItemValuePair(context.resources.getString(R.string.sprinkler_system), "", 0))
        }
        if (waterfront) {
            list.add(ItemValuePair(context.resources.getString(R.string.waterfront), "", 0))
        }
        return list
    }
}
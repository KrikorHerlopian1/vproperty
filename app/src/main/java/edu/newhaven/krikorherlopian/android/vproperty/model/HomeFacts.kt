package edu.newhaven.krikorherlopian.android.vproperty.model

import android.content.Context
import edu.newhaven.krikorherlopian.android.vproperty.R
import java.io.Serializable

data class HomeFacts(
    var homeType: String = "",
    var price: Float? = 0f, var isRent: Boolean = false,
    var isSale: Boolean = false, var bedrooms: String = "",
    var bathrooms: String? = "", var totalRooms: String? = "",
    var parkingSpaces: String? = "", var yearBuilt: String? = "",
    var hoadues: String? = "", var structuralModalYear: String? = "",
    var floorNumber: String? = "", var finishedSqFt: String? = "",
    var lotSizeFqFt: String? = "", var basementSqFt: String? = "",
    var garageSqFt: String? = ""

) : Serializable {
    fun returnListAvailable(context: Context): MutableList<ItemValuePair> {
        var list: MutableList<ItemValuePair> = mutableListOf<ItemValuePair>()
        if (bedrooms != null && !bedrooms.equals("")
            && !bedrooms.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.number_of_bedrooms), bedrooms,
                    R.drawable.ic_bed
                )
            )
        }
        if (bathrooms != null && !bathrooms.equals("")
            && !bathrooms.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.number_of_bathrooms), bathrooms,
                    R.drawable.ic_toilet
                )
            )
        }
        if (totalRooms != null && !totalRooms.equals("")
            && !totalRooms.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.total_rooms), totalRooms,
                    R.drawable.ic_living_room
                )
            )
        }
        if (yearBuilt != null && !yearBuilt.equals("")
            && !yearBuilt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.year_built), yearBuilt,
                    R.drawable.ic_2019
                )
            )
        }
        if (hoadues != null && !hoadues.equals("")
            && !hoadues.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.hoa_dues), hoadues,
                    R.drawable.ic_dollar_symbol
                )
            )
        }
        if (structuralModalYear != null && !structuralModalYear.equals(
                ""
            )
            && !structuralModalYear.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.modal_year), structuralModalYear,
                    R.drawable.ic_2019
                )
            )
        }
        if (floorNumber != null && !floorNumber.equals("")
            && !floorNumber.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.floor_number), floorNumber,
                    R.drawable.ic_format_list_numbered_black_24dp
                )
            )
        }
        if (finishedSqFt != null && !finishedSqFt.equals("")
            && !finishedSqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.finished_square_feet), finishedSqFt,
                    R.drawable.ic_full_size
                )
            )
        }
        if (lotSizeFqFt != null && !lotSizeFqFt.equals("")
            && !lotSizeFqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.lot_size), lotSizeFqFt,
                    R.drawable.ic_full_size
                )
            )
        }
        if (basementSqFt != null && !basementSqFt.equals("")
            && !basementSqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.basement_sq_ft), basementSqFt,
                    R.drawable.ic_full_size
                )
            )
        }
        if (garageSqFt != null && !garageSqFt.equals("")
            && !garageSqFt.equals("0")
        ) {
            list.add(
                ItemValuePair(
                    context.resources.getString(R.string.garage_ft), garageSqFt,
                    R.drawable.ic_full_size
                )
            )
        }
        return list
    }


}
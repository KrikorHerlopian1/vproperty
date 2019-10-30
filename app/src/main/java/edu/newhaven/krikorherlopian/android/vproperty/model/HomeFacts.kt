package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class HomeFacts(
    var homeType: String = "",
    var price: String? = "", var isRent: Boolean = false,
    var isSale: Boolean = false, var bedrooms: String = "",
    var bathrooms: String? = "", var totalRooms: String? = "",
    var parkingSpaces: String? = "", var yearBuilt: String? = "",
    var hoadues: String? = "", var structuralModalYear: String? = "",
    var floorNumber: String? = "", var finishedSqFt: String? = "",
    var lotSizeFqFt: String? = "", var basementSqFt: String? = "",
    var garageSqFt: String? = ""

) : Serializable
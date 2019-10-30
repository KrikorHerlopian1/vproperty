package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class UtilityDetails(
    var heatingType: HeatingType = HeatingType(),
    var coolingType: CoolingType = CoolingType()
) : Serializable
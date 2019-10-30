package edu.newhaven.krikorherlopian.android.vproperty.model

data class UtilityDetails(
    var heatingType: HeatingType = HeatingType(),
    var coolingType: CoolingType = CoolingType()
)
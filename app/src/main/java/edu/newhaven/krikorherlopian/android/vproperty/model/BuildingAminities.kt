package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class BuildingAminities(
    var basketballCourt: Boolean = false, var disabledAccess: Boolean = false,
    var doorman: Boolean = false, var elevator: Boolean = false,
    var fitnessCenter: Boolean = false, var sportsCourt: Boolean = false,
    var storage: Boolean = false, var tennisCourt: Boolean = false,
    var nearTransportation: Boolean = false
) : Serializable
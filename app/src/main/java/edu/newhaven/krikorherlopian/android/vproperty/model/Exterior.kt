package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class Exterior(
    var brick: Boolean = false, var cement: Boolean = false,
    var composition: Boolean = false, var metal: Boolean = false,
    var shingle: Boolean = false, var stone: Boolean = false,
    var stucco: Boolean = false, var vinyl: Boolean = false,
    var wood: Boolean = false, var woodProducts: Boolean = false,
    var other: Boolean = false
) : Serializable
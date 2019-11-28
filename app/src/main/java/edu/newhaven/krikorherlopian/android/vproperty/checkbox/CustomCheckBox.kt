package edu.newhaven.krikorherlopian.android.vproperty.checkbox

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import edu.newhaven.krikorherlopian.android.vproperty.R


class MyCustomCheckBox : AppCompatCheckBox {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context)
    private fun init() {
        val tf = Typeface.createFromAsset(
            context.assets,
            "Poppins-Light.ttf"
        )
        typeface = tf
        textSize = resources.getDimension(R.dimen.normalTextSize)
    }
}
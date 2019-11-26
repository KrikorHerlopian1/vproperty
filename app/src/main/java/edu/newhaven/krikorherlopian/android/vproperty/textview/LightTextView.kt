package edu.newhaven.krikorherlopian.android.vproperty.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class LightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    init {
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.assets, "Poppins-Light.ttf")
        }
        this.typeface = mTypeface
    }

    companion object {
        private var mTypeface: Typeface? = null
    }

}
package edu.newhaven.krikorherlopian.android.vproperty.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class BoldTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    init {

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.assets, "Poppins-Bold.ttf")
        }
        this.typeface = mTypeface
    }

    companion object {

        /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
        private var mTypeface: Typeface? = null
    }

}
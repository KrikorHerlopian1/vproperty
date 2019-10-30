package edu.newhaven.krikorherlopian.android.vproperty.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.model.HomeTypePair
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.hometype.view.*

class HomeTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    override val containerView: View?
        get() = itemView

    fun bind(item: HomeTypePair) {
        itemView.homeTypeOne.text = item.homeType1?.type
        itemView.homeTypeTwo.text = item.homeType2?.type
        if (item.homeType2 == null) {
            itemView.layout2.visibility = View.INVISIBLE
        } else
            itemView.layout2.visibility = View.VISIBLE
    }
}
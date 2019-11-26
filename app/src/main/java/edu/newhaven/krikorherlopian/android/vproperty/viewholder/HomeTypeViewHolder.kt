package edu.newhaven.krikorherlopian.android.vproperty.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.model.HomeTypes
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.hometype.view.*

class HomeTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    override val containerView: View?
        get() = itemView
    fun bind(item: HomeTypes) {
        itemView.homeTypeOne.text = item.type
    }
}
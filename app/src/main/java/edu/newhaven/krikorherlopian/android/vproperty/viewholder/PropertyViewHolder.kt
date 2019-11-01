package edu.newhaven.krikorherlopian.android.vproperty.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.property_item.view.*

class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    override val containerView: View?
        get() = itemView

    fun bind(item: Property) {
        itemView.title.text = item.houseName
        itemView.address.text = item.address.addressName
    }
}
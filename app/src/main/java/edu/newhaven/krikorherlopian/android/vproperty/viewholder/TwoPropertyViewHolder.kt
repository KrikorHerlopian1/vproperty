package edu.newhaven.krikorherlopian.android.vproperty.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.property_column.view.*


class TwoPropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    override val containerView: View?
        get() = itemView

    fun bind(item: Property) {
        itemView.subtitle.text = item.address.addressName
        itemView.price.text = "" + item.homeFacts.price + " $$"
        itemView.title.text = item.houseName
        itemView.fake_title.text = item.houseName
        itemView.fake_price.text = "" + item.homeFacts.price + " $$"
        itemView.fake_address.text = item.address.addressName

    }
}
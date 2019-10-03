package edu.newhaven.krikorherlopian.android.vproperty.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.title_subtitle_item.view.*

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    override val containerView: View?
        get() = itemView

    fun bind(item: SettingsItem) {
        itemView.title.text = item.title
        itemView.subtitle.text = item.subtitle
        itemView.image.setImageResource(item.imageId)
    }
}
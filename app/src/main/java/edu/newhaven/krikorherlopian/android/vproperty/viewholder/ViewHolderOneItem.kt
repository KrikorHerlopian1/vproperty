package edu.newhaven.krikorherlopian.android.vproperty.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.switch_item.view.*

class ViewHolderOneItem(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    override val containerView: View?
        get() = itemView

    fun bind(item: SettingsItem) {
        itemView.title.text = item.title
        itemView.switch_item.isChecked = item.subtitle.toBoolean()
        itemView.image.setImageResource(item.imageId)
    }
}
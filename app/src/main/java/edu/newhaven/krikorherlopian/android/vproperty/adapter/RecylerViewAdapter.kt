package edu.newhaven.krikorherlopian.android.vproperty.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.MyViewHolder
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.PropertyViewHolder
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.TwoPropertyViewHolder
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.ViewHolderOneItem
import kotlinx.android.synthetic.main.property_item.view.*
import kotlinx.android.synthetic.main.switch_item.view.*

/*
    This page adapter, contains an icon plus two texts below each other.
 */
class RecylerViewAdapter(
    private val list: MutableList<Any>,
    var listClick: ListClick,
    var context: Context,
    var twoColumn: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER_VIEW = 0
    private val CONTENT_VIEW = 1
    private val PROPERTY_VIEW = 2
    private val PROPERTY_VIEW_TWO_COLUMN = 3
    var lastPosition = -1
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        try {
            holder.itemView.clearAnimation()
        } catch (e: Exception) {
        }
        holder.itemView.clearAnimation()
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(position: Int, property: Property) {
        list.add(position, property)
        notifyItemRangeInserted(0, 1)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(
            context,
            if (position > lastPosition)
                R.anim.up_from_bottom
            else
                R.anim.down_from_top
        )
        holder.itemView.startAnimation(animation)
        lastPosition = position
        when (holder.itemViewType) {
            HEADER_VIEW -> configureViewHolder1(holder, position)
            CONTENT_VIEW -> configureViewHolder2(holder, position)
            PROPERTY_VIEW -> configureViewHolder3(holder, position)
            PROPERTY_VIEW_TWO_COLUMN -> configureViewHolder4(holder, position)
        }
    }

    private fun configureViewHolder4(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TwoPropertyViewHolder && list.get(position) is Property) {
            var property = list.get(position) as Property
            holder.bind(property)
            Glide.with(context).load(property.photoUrl)
                .placeholder(R.drawable.profileplaceholder)
                .into(
                    holder.itemView.thumbnail
                )
        }
        holder.itemView.setOnClickListener {
            listClick.rowClicked(
                position,
                0,
                holder.itemView.thumbnail
            )
        }
    }


    private fun configureViewHolder3(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PropertyViewHolder && list.get(position) is Property) {
            var property = list.get(position) as Property
            holder.bind(property)
            Glide.with(context).load(property.photoUrl)
                .placeholder(R.drawable.profileplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    holder.itemView.thumbnail
                )
        }
        holder.itemView.setOnClickListener {
            listClick.rowClicked(
                position,
                0,
                holder.itemView.thumbnail
            )
        }
    }

    private fun configureViewHolder1(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder && list.get(position) is SettingsItem)
            holder.bind(list.get(position) as SettingsItem)
        holder.itemView.setOnClickListener { listClick.rowClicked(position) }
    }

    private fun configureViewHolder2(holder: RecyclerView.ViewHolder, position: Int) {
        var item = list.get(position) as SettingsItem
        if (holder is ViewHolderOneItem)
            holder.bind(item)

        if (item.switch == 2) {
            holder.itemView.switch_item.visibility = View.INVISIBLE
            holder.itemView.setOnClickListener { listClick.rowClicked(position) }
            holder.itemView.switch_item.setOnCheckedChangeListener { compoundButton, b ->
            }
        } else {
            holder.itemView.switch_item.visibility = View.VISIBLE
            holder.itemView.setOnClickListener { }
            holder.itemView.switch_item.setOnCheckedChangeListener { compoundButton, b ->
                if (compoundButton.isChecked)
                    item.subtitle = "true"
                else
                    item.subtitle = "false"
                listClick.rowClicked(position)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            HEADER_VIEW -> return MyViewHolder(parent.inflate(R.layout.title_subtitle_item))
            CONTENT_VIEW -> return ViewHolderOneItem(parent.inflate(R.layout.switch_item))
            PROPERTY_VIEW -> return PropertyViewHolder(parent.inflate(R.layout.property_item))
            PROPERTY_VIEW_TWO_COLUMN -> return TwoPropertyViewHolder(parent.inflate(R.layout.property_column))
        }
        return MyViewHolder(parent.inflate(R.layout.title_subtitle_item))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        if (twoColumn)
            return PROPERTY_VIEW_TWO_COLUMN
        else if (list.get(position) is SettingsItem) {
            if ((list.get(position) as SettingsItem).switch == 0)
                return HEADER_VIEW
            else
                return CONTENT_VIEW
        } else if (list.get(position) is Property)
            return PROPERTY_VIEW
        return super.getItemViewType(position)
    }


    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }
}

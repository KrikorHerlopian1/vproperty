package edu.newhaven.krikorherlopian.android.vproperty.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.MyViewHolder
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.ViewHolderOneItem
import kotlinx.android.synthetic.main.switch_item.view.*

/*
    This page adapter, contains an icon plus two texts below each other.
 */
class TitleSubtitleAdapter(private val list: MutableList<SettingsItem>, var listClick: ListClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER_VIEW = 0
    private val CONTENT_VIEW = 1
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER_VIEW -> configureViewHolder1(holder, position)
            CONTENT_VIEW -> configureViewHolder2(holder, position)
        }

    }

    private fun configureViewHolder1(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder)
            holder.bind(list.get(position))
        holder.itemView.setOnClickListener { listClick.rowClicked(position) }
    }

    private fun configureViewHolder2(holder: RecyclerView.ViewHolder, position: Int) {
        var item = list.get(position)
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
        }
        return MyViewHolder(parent.inflate(R.layout.title_subtitle_item))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        if (list.get(position).switch == 0)
            return HEADER_VIEW
        else
            return CONTENT_VIEW
        return super.getItemViewType(position)
    }


    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }
}

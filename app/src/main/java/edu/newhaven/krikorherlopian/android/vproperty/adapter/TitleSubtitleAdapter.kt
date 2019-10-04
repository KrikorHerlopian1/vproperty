package edu.newhaven.krikorherlopian.android.vproperty.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.MyViewHolder

/*
    This page adapter, contains an icon plus two texts below each other.
 */
class TitleSubtitleAdapter(private val list: MutableList<SettingsItem>, var listClick: ListClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder)
            holder.bind(list.get(position))
        holder.itemView.setOnClickListener { listClick.rowClicked(position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(parent.inflate(R.layout.title_subtitle_item))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }
}

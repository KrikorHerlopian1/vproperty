package edu.newhaven.krikorherlopian.android.vproperty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.HomeTypePair
import edu.newhaven.krikorherlopian.android.vproperty.viewholder.HomeTypeViewHolder
import kotlinx.android.synthetic.main.hometype.view.*

class HomeTypeAdapter(private val list: MutableList<HomeTypePair>, var listClick: ListClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeTypeViewHolder) {
            holder.itemView.layout2.setOnClickListener {
                if (list.get(position).homeType2 != null)
                    listClick.rowClicked(position, 2)
            }
            holder.itemView.layout1.setOnClickListener {
                listClick.rowClicked(position, 1)
            }
            if (list.get(position).homeType2?.selected == 1) {
                holder.itemView.layout2.setBackgroundResource(R.drawable.bordertype)
            } else
                holder.itemView.layout2.setBackgroundResource(R.drawable.bordertypeselected)

            if (list.get(position).homeType1?.selected == 1) {
                holder.itemView.layout1.setBackgroundResource(R.drawable.bordertype)
            } else
                holder.itemView.layout1.setBackgroundResource(R.drawable.bordertypeselected)
            holder.bind(list.get(position))
        }
        // holder.itemView.setOnClickListener { listClick.rowClicked(position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HomeTypeViewHolder(parent.inflate(R.layout.hometype))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }
}

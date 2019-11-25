package com.trendyol.medusa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by erkut.aras on 2019-11-25.
 */
class SamplesAdapter(
    private val list: List<Pair<String, Class<*>>>,
    private val onItemClick: (clazz: Class<*>) -> Unit
) : RecyclerView.Adapter<SamplesAdapter.SampleItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleItemViewHolder {
        return SampleItemViewHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, null, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SampleItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class SampleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Pair<String, Class<*>>) {
            itemView.findViewById<TextView>(android.R.id.text1).text = item.first
            itemView.setOnClickListener { onItemClick.invoke(item.second) }
        }
    }
}
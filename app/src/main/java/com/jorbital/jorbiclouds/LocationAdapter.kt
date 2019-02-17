package com.jorbital.jorbiclouds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.location_item.view.*

class LocationAdapter(
    var items: List<YrLocation>,
    private val context: Context,
    private val onClickListener: (View, YrLocation) -> Unit
) :
    RecyclerView.Adapter<LocationHolder>() {

    fun updateItems(newItems: List<YrLocation>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        return LocationHolder(LayoutInflater.from(context).inflate(R.layout.location_item, parent, false))
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.locationName.text = items[position].name

        holder.itemView.setOnClickListener { view ->
            onClickListener(view, items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class LocationHolder(view: View) : RecyclerView.ViewHolder(view) {
    val locationName = view.todayTitle!!
}
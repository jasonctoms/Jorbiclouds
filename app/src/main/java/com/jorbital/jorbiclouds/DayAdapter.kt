package com.jorbital.jorbiclouds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.day_item.view.*

class DayAdapter(
    var items: List<JorbicloudsDay>,
    private val context: Context,
    private val onClickListener: (View, JorbicloudsDay) -> Unit
) :
    RecyclerView.Adapter<DayHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    fun updateItems(newItems: List<JorbicloudsDay>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
        return DayHolder(LayoutInflater.from(context).inflate(R.layout.day_item, parent, false))
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int) {
        holder.dateText.text = items[position].date

        holder.itemView.setOnClickListener { view ->
            onClickListener(view, items[position])
        }

        holder.weatherEntries.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = WeatherAdapter(items[position].weatherEntries, context)
            setRecycledViewPool(viewPool)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class DayHolder(view: View) : RecyclerView.ViewHolder(view) {
    val dateText = view.date!!
    val weatherEntries = view.weatherRv!!
}
package com.jorbital.jorbiclouds

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.weather_item.view.*

class WeatherAdapter(
    var items: List<WeatherEntry>,
    private val context: Context
) :
    RecyclerView.Adapter<WeatherHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        return WeatherHolder(LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false))
    }

    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
        val item = items[position]
        holder.time.text = item.timeSpan
        holder.temperature.text = item.temp.toString() + "°"
        holder.minPrecipitation.text = item.minPrecip.toString()
        holder.maxPrecipitation.text = item.maxPrecip.toString()
        holder.windSpeed.text = item.windSpeed.toString()

        val assetPath = "file:///android_asset/${item.icon}.png"
        Glide.with(context).load(Uri.parse(assetPath)).into(holder.icon)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class WeatherHolder(view: View) : RecyclerView.ViewHolder(view) {
    val time = view.timeSpan!!
    val icon = view.weatherIcon!!
    val temperature = view.temperature!!
    val minPrecipitation = view.minPrecip!!
    val maxPrecipitation = view.maxPrecip!!
    val windSpeed = view.windSpeed!!
}
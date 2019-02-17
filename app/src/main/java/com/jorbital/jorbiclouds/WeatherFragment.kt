package com.jorbital.jorbiclouds

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import retrofit2.HttpException

import kotlinx.android.synthetic.main.fragment_weather.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuInflater
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager


class WeatherFragment : Fragment() {

    lateinit var searchItem: MenuItem
    lateinit var viewModel: WeatherViewModel

    private fun toast(message: CharSequence) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        viewModel.init()
        viewModel.getSelectedLocation().observe(this, Observer<YrLocation> { location ->
            updateLocation(location)
        })
        viewModel.getListOfLocations().observe(this, Observer<List<YrLocation>> { locations ->
            updateAdapter(locations)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    // TODO: if i add any navigation, do it here
    fun navigationPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "Jorbiclouds"

        locationRv.layoutManager = LinearLayoutManager(context)

        searchForCurrentLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.weather_menu, menu)
        searchItem = menu.findItem(R.id.location_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length > 2)
                    searchForLocation(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchForLocation(query)
                return false
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                locationRv.visibility = View.GONE
                weatherInfoLayout.visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }
        })
    }

    private fun searchForLocation(query: String) {

        locationRv.visibility = View.VISIBLE
        weatherInfoLayout.visibility = View.GONE
        doHttpCall { viewModel.searchLocation(query, null, null, null, "en") }
    }

    private fun updateAdapter(locations: List<YrLocation>) {
        if (locationRv.adapter == null) {
            locationRv.adapter =
                LocationAdapter(locations, requireContext()) { _, location -> locationSelected(location) }
        } else {
            (locationRv.adapter as LocationAdapter).updateItems(locations)
        }
    }

    private fun locationSelected(location: YrLocation) {
        locationName.text = location.name
        searchItem.collapseActionView()
    }

    private fun searchForCurrentLocation() {

        //TODO: get current location for latlong
        doHttpCall { viewModel.getLocation("", 59.91273, 10.74609, 1000.0, "en") }
    }

    private fun updateLocation(location: YrLocation) {
        locationName.text = location.name
    }

    private fun doHttpCall(vmHttpMethod: () -> Unit) {
        try {
            vmHttpMethod()
        } catch (e: HttpException) {
            toast(e.code().toString())
        } catch (e: Throwable) {
            toast("Oops, something unknown went wrong!")
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

}

package com.jorbital.jorbiclouds

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*

class WeatherFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var searchItem: MenuItem
    private lateinit var viewModel: WeatherViewModel

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
            updateLocationAdapter(locations)
        })
        viewModel.getJorbicloudsDays().observe(this, Observer<List<JorbicloudsDay>> { days ->
            updateDayAdapter(days)
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    doHttpCall { viewModel.getLocation("", location.latitude, location.longitude, 1000.0, "en") }
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }
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

        locationRv.layoutManager = LinearLayoutManager(context)
        weatherRv.layoutManager = LinearLayoutManager(context)
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
                weatherRv.visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.current_location) {
            getCurrentLocation()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchForLocation(query: String) {
        locationRv.visibility = View.VISIBLE
        weatherRv.visibility = View.GONE
        doHttpCall { viewModel.searchLocation(query, null, null, null, "en") }
    }

    private fun updateLocationAdapter(locations: List<YrLocation>) {
        if (locationRv.adapter == null) {
            locationRv.adapter =
                LocationAdapter(locations, requireContext()) { _, location -> locationSelected(location) }
        } else
            (locationRv.adapter as LocationAdapter).updateItems(locations)
    }

    private fun locationSelected(location: YrLocation) {
        updateLocation(location)
        searchItem.collapseActionView()
    }

    private fun updateLocation(location: YrLocation) {
        (activity as AppCompatActivity).supportActionBar?.title = location.name
        //TODO: add some progress spinner or something
        doHttpCall { viewModel.getForecast(location.id) }
    }

    private fun updateDayAdapter(days: List<JorbicloudsDay>) {
        if (weatherRv.adapter == null) {
            weatherRv.adapter =
                DayAdapter(days, requireContext()) { _, day -> toast("This would open details of " + day.date) }
        } else
            (weatherRv.adapter as DayAdapter).updateItems(days)
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

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions()
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            1
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //TODO: handle if they deny the permission
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

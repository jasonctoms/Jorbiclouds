package com.jorbital.jorbiclouds

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), WeatherFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        //dont really have navigation yet
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

package com.ridesharingapp.passengersideapp.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ridesharingapp.passengersideapp.R
import com.ridesharingapp.passengersideapp.databinding.FragmentPassengerDashboardBinding

class PassengerDashboardFragment : Fragment(R.layout.fragment_passenger_dashboard) {

    lateinit var binding: FragmentPassengerDashboardBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPassengerDashboardBinding.bind(view)
    }
}
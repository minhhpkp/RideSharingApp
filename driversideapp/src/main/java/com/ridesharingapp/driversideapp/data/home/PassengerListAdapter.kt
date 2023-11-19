package com.ridesharingapp.driversideapp.data.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.maps.model.LatLng
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.Ride
import com.ridesharingapp.driversideapp.R
import com.ridesharingapp.driversideapp.databinding.ListItemPassengerBinding

class PassengerListAdapter : ListAdapter<Pair<GrabLamUser, String>, PassengerListAdapter.PassengerViewHolder>(
    object: DiffUtil.ItemCallback<Pair<GrabLamUser, String>>() {
        override fun areItemsTheSame(oldItem: Pair<GrabLamUser, String>, newItem: Pair<GrabLamUser, String>): Boolean {
            return oldItem.first.userId == newItem.first.userId
        }

        override fun areContentsTheSame(oldItem: Pair<GrabLamUser, String>, newItem: Pair<GrabLamUser, String>): Boolean {
            return oldItem.first == newItem.first
        }
    }
) {

    var handleItemClick: ((GrabLamUser) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        return PassengerViewHolder(
            ListItemPassengerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        getItem(position).apply {
            holder.username.text = first.username
            holder.distance.text = buildString {
                append(holder.itemView.context.getString(R.string.passenger_is))
                append(second)
            }

            holder.layout.setOnClickListener { handleItemClick?.invoke(first) }
        }
    }

    inner class PassengerViewHolder constructor(binding: ListItemPassengerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val username: TextView = binding.username
        val distance: TextView = binding.passengerDistance
        val layout: View = binding.listItemLayout
    }
}
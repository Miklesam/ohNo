package com.onelinegaming.walkietalk.deviceList

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onelinegaming.walkietalk.R

class DeviceViewHolder(itemView: View, var deviceListener: OnDeviceClicked) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val deviceName: TextView
    val connectionStatus: TextView

    init {
        deviceName = itemView.findViewById(R.id.device_name)
        connectionStatus = itemView.findViewById(R.id.device_status)
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        deviceListener.onClick(adapterPosition)
    }
}
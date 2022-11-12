package com.onelinegaming.walkietalk.deviceList

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.onelinegaming.walkietalk.R
import com.onelinegaming.walkietalk.scan.DirectDevices

class DeviceAdapter(val onDeviceClicked: OnDeviceClicked) :
    RecyclerView.Adapter<DeviceViewHolder>() {

    private var deviceList: List<DirectDevices>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_view_item, parent, false)
        return DeviceViewHolder(itemView, onDeviceClicked)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        if (deviceList == null) return
        val device = deviceList!![position]
        holder.deviceName.text = device.name
        holder.connectionStatus.text = when (device.status) {
            0 -> "Connected"
            1 -> "Invited"
            2 -> "Failed"
            3 -> "AVAILABLE"
            4 -> "UNAVAILABLE"
            else -> "unknown"
        }
    }

    override fun getItemCount(): Int {
        return deviceList?.size ?: 0
    }

    fun setDevices(it: List<DirectDevices>) {
        deviceList = it
        notifyDataSetChanged()
    }

}
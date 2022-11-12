package com.onelinegaming.walkietalk.scan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.onelinegaming.walkietalk.MainActivity
import com.onelinegaming.walkietalk.R
import com.onelinegaming.walkietalk.deviceList.DeviceAdapter
import com.onelinegaming.walkietalk.deviceList.OnDeviceClicked
import kotlinx.android.synthetic.main.fragment_scan.*

class ScanFragment : Fragment(R.layout.fragment_scan), OnDeviceClicked {

    private val viewModel: ScanViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        devices_list.layoutManager = LinearLayoutManager(requireContext())
        val deviceAdapter = DeviceAdapter(this)
        devices_list.adapter = deviceAdapter

        viewModel.getDeviceList().observe(viewLifecycleOwner) {
            deviceAdapter.setDevices(it)
        }

        viewModel.goToTalk.observe(viewLifecycleOwner) {
            (activity as MainActivity).openTalkFragment()
        }

        bttn_to_talk.setOnClickListener {
            viewModel.scanDevices()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onClick(position: Int) {
        viewModel.connectToDevice(position)
    }

}
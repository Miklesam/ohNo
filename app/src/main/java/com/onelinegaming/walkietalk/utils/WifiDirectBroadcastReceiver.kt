package com.onelinegaming.walkietalk.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.util.Log
import android.widget.Toast

class WifiDirectBroadcastReceiver(val p2pManager: WifiP2pManager,
                                  val channel : WifiP2pManager.Channel,
                                  val peersListener : PeerListListener,
                                  val connectionInfoListener: ConnectionInfoListener
) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            val state: Int = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "WIFI is On", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "WIFI is OFF", Toast.LENGTH_SHORT).show()
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
            if (p2pManager != null) {
                p2pManager.requestPeers(channel, peersListener)
                //mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
                Log.e("DEVICE_NAME", "WIFI P2P peers changed called")
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            if (p2pManager == null) {
                return
            }
            val networkInfo =
                intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
            if (networkInfo != null && networkInfo.isConnected) {
                p2pManager.requestConnectionInfo(channel, connectionInfoListener)
            } else {
                //mActivity.connectionStatus.setText("Device Disconnected")
                //mActivity.clear_all_device_icons()
                //mActivity.rippleBackground.stopRippleAnimation()
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
        }
    }
}
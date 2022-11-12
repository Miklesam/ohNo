package com.onelinegaming.walkietalk.scan

import android.Manifest
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_P2P_SERVICE
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.*
import android.os.Looper.getMainLooper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.onelinegaming.walkietalk.utils.SingleLiveEvent
import com.onelinegaming.walkietalk.utils.SocketHandler
import com.onelinegaming.walkietalk.utils.WifiDirectBroadcastReceiver
import kotlinx.coroutines.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val PORT_USED = 9584
    }

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var wifiManager: WifiManager? = null
    var mManager: WifiP2pManager
    var mChannel: Channel

    var mReceiver: BroadcastReceiver
    var mIntentFilter: IntentFilter

    val context : Context

    val deviceList = MutableLiveData<List<DirectDevices>>()
    fun getDeviceList(): LiveData<List<DirectDevices>> = deviceList
    val goToTalk: SingleLiveEvent<Unit> = SingleLiveEvent()

    val peerListListener = PeerListListener { peersList ->
        Log.d("DEVICE_NAME", "Listener called " + peersList.deviceList.size)
        val devices: ArrayList<DirectDevices> = ArrayList<DirectDevices>()
        if (peersList.deviceList.isNotEmpty()) {
            for (device in peersList.deviceList) {
                devices.add(DirectDevices(
                    name = device.deviceName,
                    deviceAdress = device.deviceAddress,
                    status = device.status
                ))
            }
        }
        deviceList.value = devices
    }

    val connectionInfoListener = ConnectionInfoListener { info ->
        scope.launch {
            scope.launch {
                if (info.groupFormed && info.isGroupOwner) {
                    createServer()
                } else if (info.groupFormed) {
                    createClient(info.groupOwnerAddress)
                }
                goToTalk.postValue(Unit)
            }
        }
    }

    init {
        context = application.applicationContext
        mManager = application.applicationContext.getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mManager.initialize(application.applicationContext, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver(mManager, mChannel, peerListListener,connectionInfoListener)
        mIntentFilter = IntentFilter()
        mIntentFilter.addAction(WIFI_P2P_STATE_CHANGED_ACTION)
        mIntentFilter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION)
        mIntentFilter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION)
        mIntentFilter.addAction(WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    fun scanDevices(){
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mManager.discoverPeers(mChannel, object : ActionListener{
            override fun onSuccess() {
                Log.w("ScanViewModel", "Success Discover")
            }

            override fun onFailure(p0: Int) {
                Log.w("ScanViewModel", "Fail Discover ")
            }

        })
    }

    fun connectToDevice(index : Int){
        val listDevices = deviceList.value
        val device = listDevices?.get(index)
        val config = WifiP2pConfig()
        config.deviceAddress = device?.deviceAdress
        config.wps.setup = WpsInfo.PBC
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mManager.connect(mChannel,config,object : ActionListener{
            override fun onSuccess() {
                Log.w("ScanViewModel", "Connect Success")
            }

            override fun onFailure(p0: Int) {
                Log.w("ScanViewModel", "Connect Failure")
            }

        })
    }

    fun onResume(){
        mReceiver = WifiDirectBroadcastReceiver(mManager, mChannel, peerListListener,connectionInfoListener)
        context.registerReceiver(mReceiver, mIntentFilter)
    }

    fun onPause(){
        context.unregisterReceiver(mReceiver)
    }

    override fun onCleared() {
        super.onCleared()
        mManager.removeGroup(mChannel, object : ActionListener{
            override fun onSuccess() {
                Log.w("ScanViewModel", "removeGroup Success")
            }

            override fun onFailure(p0: Int) {
                Log.w("ScanViewModel", "removeGroup onFailure")
            }

        })
    }

    private suspend fun createServer(){
        val serverSocket = withContext(Dispatchers.IO) {
            ServerSocket(PORT_USED)
        }
        val socket = withContext(Dispatchers.IO) {
            serverSocket.accept()
        }
        SocketHandler.setSocket(socket)
    }

    private suspend fun createClient(address: InetAddress){
        val socket = Socket()
        withContext(Dispatchers.IO) {
            socket.connect(InetSocketAddress(address.hostAddress, PORT_USED), 500)
        }
        SocketHandler.setSocket(socket)
    }
}


data class DirectDevices(
    val name : String,
    val deviceAdress : String,
    val status : Int
    )


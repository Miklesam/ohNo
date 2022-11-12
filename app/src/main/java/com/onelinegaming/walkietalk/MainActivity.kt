package com.onelinegaming.walkietalk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {

    companion object{
        private const val MY_PERMISSIONS_REQUEST_REQUIRED_PERMISSION = 3
    }

    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        getPermissions()
    }

    fun openTalkFragment() {
        navController?.navigate(R.id.action_scanFragment_to_talkFragment)
    }

    fun getPermissions() {
        if ((checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    !== PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_REQUIRED_PERMISSION
            )
        }
    }
}
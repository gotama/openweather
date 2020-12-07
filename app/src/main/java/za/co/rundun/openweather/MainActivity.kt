package za.co.rundun.openweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import za.co.rundun.openweather.data.viewmodel.SharedViewModel
import za.co.rundun.openweather.service.WeatherLocationService

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnSuccessListener<Location>, PermissionsFragmentCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var foregroundOnlyBroadcastReceiver: WeatherLocationServiceBroadcastReceiver
    private var weatherLocationService: WeatherLocationService? = null
    private var weatherLocationServiceBound = false
    private val sharedViewModel: SharedViewModel by viewModels()
    private var lastLocation: Location? = null;

    companion object {
        private const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO move location services to MainApp
        foregroundOnlyBroadcastReceiver = WeatherLocationServiceBroadcastReceiver()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        updateCoordinateFromBundle(savedInstanceState)

//        val navController = findNavController(this, R.id.nav_host_fragment)
        sharedViewModel.selectedItem.observe(this, Observer { item ->
            if (item == 2) {
                updateLocation()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putParcelable(REQUESTING_LOCATION_UPDATES_KEY, lastLocation)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, WeatherLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                WeatherLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (weatherLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            weatherLocationServiceBound = false
        }
        super.onStop()
    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            sharedViewModel.getCurrentWeather(location.latitude, location.longitude)
        } else if (onCheckPermissions()) {
            weatherLocationService?.subscribeToLocationUpdates()
        }
    }

    override fun onCheckPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return if (result == PackageManager.PERMISSION_GRANTED &&
            result1 == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            val i = Intent(this, SplashActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            false
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        if (onCheckPermissions()) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this, this)
        }
    }

    private fun updateCoordinateFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            lastLocation = savedInstanceState.getParcelable(REQUESTING_LOCATION_UPDATES_KEY)
        }
    }

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as WeatherLocationService.LocalBinder
            weatherLocationService = binder.service
            weatherLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            weatherLocationService = null
            weatherLocationServiceBound = false
        }
    }

    private inner class WeatherLocationServiceBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                WeatherLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                sharedViewModel.getCurrentWeather(location.latitude, location.longitude)
            }
        }
    }
}

interface PermissionsFragmentCallback {
    fun onCheckPermissions(): Boolean
}
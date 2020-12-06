package za.co.rundun.openweather

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import za.co.rundun.openweather.data.viewmodel.SharedViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import za.co.rundun.openweather.common.SharedPreferenceUtil
import za.co.rundun.openweather.service.WeatherLocationService

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    OnSuccessListener<Location>,
    SharedPreferences.OnSharedPreferenceChangeListener,
    PermissionsFragmentCallback {

    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var foregroundOnlyBroadcastReceiver: WeatherLocationServiceBroadcastReceiver
    private var weatherLocationService: WeatherLocationService? = null
    private var weatherLocationServiceBound = false
    private val sharedViewModel: SharedViewModel by viewModels()

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foregroundOnlyBroadcastReceiver = WeatherLocationServiceBroadcastReceiver()
        sharedPreferences =
            getSharedPreferences(getString(R.string.location_preference_key), Context.MODE_PRIVATE)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

//        updateValuesFromBundle(savedInstanceState)

        val navController = findNavController(this, R.id.nav_host_fragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav.setupWithNavController(navController)
        sharedViewModel.selectedItem.observe(this, Observer { item ->
            if (item == 1) {
                requestPermissions()
            } else if (item == 2) {
                updateLocation()
            }
        })
    }
//    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
//        savedInstanceState ?: return
//
//        // Update the value of requestingLocationUpdates from the Bundle.
//        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//            requestingLocationUpdates = savedInstanceState.getBoolean(
//                    REQUESTING_LOCATION_UPDATES_KEY)
//        }
//    }
//
//
//    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
//        super.onSaveInstanceState(outState, outPersistentState)
//    }

    override fun onStart() {
        super.onStart()

//        updateButtonState(
//            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
//        )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, WeatherLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                WeatherLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
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
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                if (checkPermission()) {
                    updateLocation()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                DialogInterface.OnClickListener { _, _ ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions()
                                    }
                                })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun updateLocation() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this, this)
        } else {
            requestPermissions()
        }
    }

    override fun onCheckPermissions(): Boolean {
        return checkPermission()
    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            sharedViewModel.getCurrentWeather(location.latitude, location.longitude)
        } else if(checkPermission()) {
            weatherLocationService?.subscribeToLocationUpdates()
        }
    }

    private inner class WeatherLocationServiceBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                WeatherLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                sharedViewModel.getCurrentWeather(location.latitude, location.longitude)
//                logResultsToScreen("Foreground location: ${location.toText()}")
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {
//            updateButtonState(sharedPreferences.getBoolean(
//                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
//            )
        }
    }
}

interface PermissionsFragmentCallback {
    fun onCheckPermissions(): Boolean
}
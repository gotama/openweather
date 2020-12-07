package za.co.rundun.openweather.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import za.co.rundun.openweather.PermissionsFragmentCallback
import za.co.rundun.openweather.R
import za.co.rundun.openweather.data.viewmodel.SharedViewModel
import za.co.rundun.openweather.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import za.co.rundun.openweather.data.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var fragmentCallback: PermissionsFragmentCallback
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallback = context as PermissionsFragmentCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        // TODO Investigate purpose
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedViewModel = sharedViewModel
        binding.homeViewModel = homeViewModel

        sharedViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }
        sharedViewModel.weather.observe(viewLifecycleOwner) { weatherList ->
            var url = "https://openweathermap.org/img/w/" + weatherList[0].icon + ".png"
            homeViewModel.imageUrl = url
            binding.weatherMain.text = weatherList[0].main
            binding.weatherDescription.text = weatherList[0].description
            binding.weatherWindSpeed.text = weatherList[0].windSpeed.toString()
            binding.weatherWindDegree.text = weatherList[0].windDegree.toString()
            binding.invalidateAll()
        }

        binding.setClickListener { view ->
            when (view.id) {
                R.id.what_weather_button -> if (fragmentCallback.onCheckPermissions()) {
                    sharedViewModel.selectItem(2)
                } else {
                    sharedViewModel.selectItem(1)
                }
                R.id.what_weather_card -> {

                }// TODO navigate to weather detail
            }
        }

        return binding.root
    }
}
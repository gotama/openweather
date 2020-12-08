package za.co.rundun.openweather.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import za.co.rundun.openweather.PermissionsFragmentCallback
import za.co.rundun.openweather.R
import za.co.rundun.openweather.data.viewmodel.HomeViewModel
import za.co.rundun.openweather.data.viewmodel.SharedViewModel
import za.co.rundun.openweather.databinding.FragmentHomeBinding

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
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedViewModel = sharedViewModel
        binding.homeViewModel = homeViewModel

        sharedViewModel.spinner.observe(viewLifecycleOwner) { show ->
            if (show) {
                binding.spinner.alpha = 0f
                binding.whatWeatherButton.alpha = 1f
                binding.spinner.visibility = View.VISIBLE
                binding.spinner.animate().alpha(1f).duration = 500
                binding.whatWeatherButton.animate().alpha(0f).setDuration(500).withEndAction {
                    binding.whatWeatherButton.visibility = View.GONE
                }
            } else {
                binding.spinner.alpha = 1f
                binding.whatWeatherButton.alpha = 0f
                binding.whatWeatherButton.visibility = View.VISIBLE
                binding.spinner.animate().alpha(0f).setDuration(500).withEndAction {
                    binding.spinner.visibility = View.GONE
                }
                binding.whatWeatherButton.animate().alpha(1f).duration = 500
            }

        }
        sharedViewModel.weather.observe(viewLifecycleOwner) { weatherList ->

            homeViewModel.imageUrl =
                "https://openweathermap.org/img/w/" + weatherList[0].icon + ".png"
            ("Expect " + weatherList[0].main).also { binding.weatherMain.text = it }
            binding.weatherDescription.text = weatherList[0].description
            ("Surface measured at " + weatherList[0].temp.toString() + "\u2103").also {
                binding.weatherTemperature.text = it
            }
            ("Atmosphere feels like " + weatherList[0].feelsLike.toString() + "\u2103").also {
                binding.weatherFeelsLikeTemperature.text = it
            }
            ("Wind speed " + weatherList[0].windSpeed.toString() + " m/sec").also {
                binding.weatherWindSpeed.text = it
            }
            ("Wind coming from " + weatherList[0].windDegree.toString() + "\u00B0").also {
                binding.weatherWindDegree.text = it
            }
            binding.windDegreeAngle.updateDegreeAngle(
                weatherList[0].windDegree.toFloat(),
                weatherList[0].windSpeed.toFloat()
            )
            binding.invalidateAll()
        }

        binding.setClickListener { view ->
            when (view.id) {
                R.id.what_weather_button -> if (fragmentCallback.onCheckPermissions()) {
                    sharedViewModel.selectItem(2)
                }
                R.id.what_weather_card -> {
                    // TODO navigate to weather detail
                }
            }
        }

        return binding.root
    }
}
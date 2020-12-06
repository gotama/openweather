package com.example.openweather.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.openweather.PermissionsFragmentCallback
import com.example.openweather.R
import com.example.openweather.data.viewmodel.SharedViewModel
import com.example.openweather.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // TODO
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Button -> get the current weather
//        // TextVIew -> updates the state
//        // Animate -> animate through states
//        // Navigate to CurrentWeatherFragment
//    }

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
//        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }
        sharedViewModel.weather.observe(viewLifecycleOwner) { weatherList ->
            var url = "http://openweathermap.org/img/w/" + weatherList[0].icon + ".png"
            binding.weatherImage.setImageURI(Uri.parse(url))
            binding.weatherDescription.text = weatherList[0].base
        }

// TODO Remove HomeViewModel
//        viewModel.imageUrl.observe(viewLifecycleOwner, Observer { newImgUrl ->
//            binding.weatherImage.setImageURI(Uri.parse(newImgUrl))
//        })
//        viewModel.base.observe(viewLifecycleOwner, Observer { newBase ->
//            binding.weatherDescription.text = newBase
//        })

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
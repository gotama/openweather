package za.co.rundun.openweather.data.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import za.co.rundun.openweather.common.WeatherResult
import za.co.rundun.openweather.data.database.Weather
import za.co.rundun.openweather.data.database.WeatherRepository
import kotlinx.coroutines.launch

class SharedViewModel @ViewModelInject internal constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableLiveData<List<Weather>>()
    val weather: LiveData<List<Weather>>
        get() = _weather

    private val _spinner = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val mutableSelectedItem = MutableLiveData<Int>()
    val selectedItem: LiveData<Int> get() = mutableSelectedItem
    fun selectItem(item: Int) {
        mutableSelectedItem.value = item
    }

    private var _imageUrl:  String? = null
    var imageUrl
        get() = _imageUrl
        set(value) {
            _imageUrl = value
        }

    fun getCurrentWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _spinner.value = true
            val weatherResult = weatherRepository.fetchRecentWeather(latitude, longitude)
            when (weatherResult) {
                is WeatherResult.Value -> {
                    _weather.value = weatherResult.value
                    _spinner.value = false
                }
                is WeatherResult.Error ->  {
                    _snackbar.value = weatherResult.error.message
                    _spinner.value = false
                }
            }
        }
    }
}
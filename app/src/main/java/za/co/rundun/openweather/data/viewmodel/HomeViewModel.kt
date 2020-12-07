package za.co.rundun.openweather.data.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class HomeViewModel @ViewModelInject constructor() : ViewModel() {

    private var _imageUrl:  String? = null
    var imageUrl
        get() = _imageUrl
        set(value) {
            _imageUrl = value
        }
}
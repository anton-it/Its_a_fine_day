package com.ak87.itsafineday

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ak87.itsafineday.adapters.WeatherModel

class MainViewModel: ViewModel() {

    val liveDataCurrent = MutableLiveData<WeatherModel>()
    val liveDataList = MutableLiveData<List<WeatherModel>>()
}
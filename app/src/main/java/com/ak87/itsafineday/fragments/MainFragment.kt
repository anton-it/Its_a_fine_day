package com.ak87.itsafineday.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.ak87.itsafineday.DialogManager
import com.ak87.itsafineday.MainViewModel
import com.ak87.itsafineday.R
import com.ak87.itsafineday.adapters.VpAdapter
import com.ak87.itsafineday.adapters.WeatherModel
import com.ak87.itsafineday.databinding.FragmentHoursBinding
import com.ak87.itsafineday.databinding.FragmentMainBinding
import com.ak87.itsafineday.isPermissionGranted
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject


class MainFragment : Fragment() {

    private lateinit var fLocationClient: FusedLocationProviderClient
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    //    private lateinit var model: MainViewModel
    private val model: MainViewModel by activityViewModels()

    private val fragmentList = listOf<Fragment>(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )

    private val tabNameList by lazy {
        listOf(
            getString(R.string.Hours),
            getString(R.string.Days)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        model = ViewModelProvider(this)[MainViewModel::class.java]
        checkPermission()
        init()
        updateCurrentCard()
        //requestWeatherData("London")
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun init() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity, fragmentList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, pos ->
            tab.text = tabNameList[pos]
        }.attach()
        ibSync.setOnClickListener {
            tabLayout.selectTab(tabLayout.getTabAt(0))
            checkLocation()
        }
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener{
                override fun onClick() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
//        if(!isLocationEnabled()) {
//            Toast.makeText(requireContext(), getString(R.string.location_disabled), Toast.LENGTH_LONG).show()
//            return
//        }
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            Log.d("MyLog111", "Return")
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener{
//                Log.d("MyLog111", "${it.result.latitude},${it.result.longitude}")
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
                //requestWeatherData("London")
            }
    }

    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            val maxMinTemp = "${it.maxTemp}°C/${it.minTemp}°C"
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { maxMinTemp }
            tvCondition.text = it.condition
            tvMaxMin.text = if(it.currentTemp.isEmpty()) "" else maxMinTemp
            Picasso.get().load(HTTPS_URL + it.imageUrl).into(imWeather)
        }
    }

    private fun permissionListener() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String) {
        val url = "http://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"

        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                parseWeatherData(result)
            },
            { error ->
                Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {

        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
        try {
            val item = WeatherModel(
                mainObject.getJSONObject("location").getString("name"),
                mainObject.getJSONObject("current").getString("last_updated"),
                mainObject.getJSONObject("current")
                    .getJSONObject("condition").getString("text"),
                mainObject.getJSONObject("current").getString("temp_c"),
                weatherItem.maxTemp,
                weatherItem.minTemp,
                mainObject.getJSONObject("current")
                    .getJSONObject("condition").getString("icon"),
                weatherItem.dataHours
            )
            model.liveDataCurrent.value = item
            Log.d("MyLog", "City: ${item.city}")
            Log.d("MyLog", "Last update: ${item.time}")
            Log.d("MyLog", "Condition: ${item.condition}")
            Log.d("MyLog", "Current temp: ${item.currentTemp}")
            Log.d("MyLog", "Image: ${item.imageUrl}")
            Log.d("MyLog", "===================================")
            Log.d("MyLog", "Max: ${item.maxTemp}")
            Log.d("MyLog", "Min: ${item.minTemp}")
            Log.d("MyLog", "Condition: ${item.dataHours}")


        } catch (e: Exception) {
            throw throw IllegalArgumentException(
                "Error json arguments"
            )
        }

    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val name = mainObject.getJSONObject("location").getString("name")
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
            "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }


    companion object {

        const val API_KEY = "c5c542ee18d54e5981872158231506"
        const val HTTPS_URL = "https:"

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}



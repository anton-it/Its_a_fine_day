package com.ak87.itsafineday.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ak87.itsafineday.MainViewModel
import com.ak87.itsafineday.R
import com.ak87.itsafineday.adapters.WeatherAdapter
import com.ak87.itsafineday.adapters.WeatherModel
import com.ak87.itsafineday.databinding.FragmentHoursBinding
import com.ak87.itsafineday.databinding.FragmentMainBinding
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
//    private lateinit var model: MainViewModel
//    private val model by lazy {
//        ViewModelProvider(this)[MainViewModel::class.java]
//    }

    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        updateRecyclerViewHours()
    }

    private fun updateRecyclerViewHours() {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            Log.d("MyLog111", it.dataHours)
            adapter.submitList(getHoursList(it))
        }

    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val hoursArray = JSONArray(wItem.dataHours)
        val list = ArrayList<WeatherModel>()
        for(i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                wItem.city,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c"),
                "",
                "",
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("icon"),
                "",
            )
            list.add(item)
        }
        Log.d("MyLog111", "List:$list")
        return list
    }

    private fun initRecyclerView() = with(binding){
//        val list = mutableListOf<WeatherModel>()
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter()
        rcView.adapter = adapter
//        for (i in 0 until 20) {
//            val item = WeatherModel(
//                "", "$i:$i",
//                "Sunny $i", "$i Sunny",
//                "", "",
//                "", ""
//            )
//            list.add(item)
//        }

        ////////////////////////////////////////////////////////

//        val list = listOf(
//            WeatherModel(
//                "", "12:00",
//                "Sunny", "25C",
//                "", "",
//                "", ""),
//            WeatherModel(
//                "", "13:00",
//                "Sunny", "25C",
//                "", "",
//                "", ""),
//            WeatherModel(
//                "", "14:00",
//                "No sunny", "30C",
//                "", "",
//                "", "")
//        )
//        adapter.submitList(list)
    }

    companion object {

        fun newInstance() = HoursFragment()

    }
}
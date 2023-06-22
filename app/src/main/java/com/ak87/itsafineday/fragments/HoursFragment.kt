package com.ak87.itsafineday.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ak87.itsafineday.R
import com.ak87.itsafineday.adapters.WeatherAdapter
import com.ak87.itsafineday.adapters.WeatherModel
import com.ak87.itsafineday.databinding.FragmentHoursBinding
import com.ak87.itsafineday.databinding.FragmentMainBinding

class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter


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
    }

    private fun initRecyclerView() = with(binding){
        val list = mutableListOf<WeatherModel>()
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter()
        rcView.adapter = adapter
        for (i in 0 until 20) {
            val item = WeatherModel(
                "", "$i:$i",
                "Sunny $i", "$i Sunny",
                "", "",
                "", ""
            )
            list.add(item)
        }
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
        adapter.submitList(list)
    }

    companion object {

        fun newInstance() = HoursFragment()

    }
}
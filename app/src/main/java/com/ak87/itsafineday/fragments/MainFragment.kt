package com.ak87.itsafineday.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.ak87.itsafineday.R
import com.ak87.itsafineday.adapters.VpAdapter
import com.ak87.itsafineday.databinding.FragmentMainBinding
import com.ak87.itsafineday.isPermissionGranted
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {

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

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
    }

    private fun init() = with(binding) {
        val adapter = VpAdapter(activity as FragmentActivity, fragmentList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) {
            tab, pos -> tab.text = tabNameList[pos]
        }.attach()
    }

    private fun permissionListener() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
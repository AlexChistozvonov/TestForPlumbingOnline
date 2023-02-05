package com.example.test.presentation.information

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.test.R
import com.example.test.databinding.InformationFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : Fragment(R.layout.information_fragment) {

    private val args: InformationFragmentArgs by navArgs()

    private val binding by viewBinding(InformationFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        if (args.lat == null) {
            tvInformation.text = getString(R.string.point_not_set)
        } else {
            tvInformation.text =
                getString(R.string.address, args.address, args.lat, args.lon)
        }

        btnShowOnMap.setOnClickListener {
            findNavController().navigate(InformationFragmentDirections.openMapFragment())
        }
    }
}
package com.elecguitar.android.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elecguitar.android.databinding.FragmentBenefitBinding

class BenefitFragment : Fragment() {

    private lateinit var binding: FragmentBenefitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBenefitBinding.inflate(inflater, container, false)
        return binding.root
    }
}
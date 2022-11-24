package com.elecguitar.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.elecguitar.android.R
import com.elecguitar.android.databinding.FragmentEasterEggBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EasterEggBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEasterEggBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEasterEggBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val TAG = "EasterEggBottomFragment"
        fun newInstance(): EasterEggBottomFragment{
            return EasterEggBottomFragment()
        }
    }
}
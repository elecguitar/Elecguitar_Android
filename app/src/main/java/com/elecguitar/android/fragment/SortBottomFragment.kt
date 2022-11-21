package com.elecguitar.android.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.databinding.FragmentSortBottomBinding
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SortBottomFragment : BottomSheetDialogFragment() {

    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentSortBottomBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSortBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            layoutPriceAsc.setOnClickListener {
                mainViewModel.carList.sortByPriceAsc()
                dismiss()
            }
            layoutPriceDesc.setOnClickListener {
                mainViewModel.carList.sortByPriceDesc()
                dismiss()
            }
            layoutMileageAsc.setOnClickListener {
                mainViewModel.carList.sortByMileageAsc()
                dismiss()
            }
            layoutMileageDesc.setOnClickListener {
                mainViewModel.carList.sortByMileageDesc()
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "SortBottomFragment"
        fun newInstance(): SortBottomFragment{
            return SortBottomFragment()
        }
    }
}
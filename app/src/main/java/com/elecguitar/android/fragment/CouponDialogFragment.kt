package com.elecguitar.android.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.databinding.FragmentCouponDialogBinding

class CouponDialogFragment() : DialogFragment() {

    private lateinit var binding: FragmentCouponDialogBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCouponDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler(requireContext().mainLooper)

        binding.apply {

            handler.postDelayed({
                layoutGift.visibility = View.GONE
                layoutCoupon.visibility = View.VISIBLE
            }, 3200)

            btnOk.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "CouponDialogFragment"
        fun newInstance(): CouponDialogFragment{
            return CouponDialogFragment()
        }
    }
}

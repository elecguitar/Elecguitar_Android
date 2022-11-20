package com.elecguitar.android.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.adapter.CarAdapter
import com.elecguitar.android.databinding.FragmentHomeBinding
import com.elecguitar.android.dto.Car

class HomeFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentHomeBinding

    private var datas: MutableList<Car> = mutableListOf()
    private lateinit var carAdapter: CarAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (i in 1..10) {
            datas.add(Car(1, "현대 아이오닉 6(1세대)", "https://img1.daumcdn.net/thumb/R380x0/?fname=%2Fmedia%2Fvitraya%2Fauto%2Fimage%2F2eafbb%2F646B3028778B8B46D4BD0948BC307C896BF0752E642D201AC8_3L8X", "2023", 5200, 6.2f, 0, "시판"))
        }

        carAdapter = CarAdapter(mainActivity, datas)

        carAdapter.onItemClickListener = object : CarAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                // TODO : 상세 화면으로 이동
            }
        }

        binding.recyclerview.apply {
            layoutManager = GridLayoutManager(mainActivity, 2)
            adapter = carAdapter
        }
    }
}
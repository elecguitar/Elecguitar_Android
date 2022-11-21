package com.elecguitar.android.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.adapter.CarAdapter
import com.elecguitar.android.databinding.FragmentHomeBinding
import com.elecguitar.android.dto.Car
import com.elecguitar.android.service.CarListService

private const val TAG = "HomeFragment_싸피"
class HomeFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentHomeBinding

    private var datas: List<Car> = mutableListOf()
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

        CarListService().getAllCarList().observe(viewLifecycleOwner) { datas ->
            carAdapter = CarAdapter(mainActivity, datas)

            binding.recyclerview.apply {
                layoutManager = GridLayoutManager(mainActivity, 2)
                adapter = carAdapter
            }

            carAdapter.onItemClickListener = object : CarAdapter.OnItemClickListener {
                override fun onClick(view: View, position: Int) {
                    // TODO : 상세 화면으로 이동
                }
            }
        }
    }

//    inner class GetAllCarListCallback : RetrofitCallback<Car> {
//        override fun onSuccess(code: Int, responseData: Car) {
//            if (responseData != null) {
//
//            } else {
//                Toast.makeText(context, "차가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        override fun onError(t: Throwable) {
//            Log.d(TAG, t.message ?: "전체 차 정보 불러오는 중 통신 오류")
//        }
//
//        override fun onFailure(code: Int) {
//            TODO("Not yet implemented")
//        }
//
//    }
}
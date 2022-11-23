package com.elecguitar.android.fragment

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.adapter.CarAdapter
import com.elecguitar.android.databinding.FragmentHomeBinding
import com.elecguitar.android.dto.Car
import com.elecguitar.android.service.CarListService
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.viewmodel.MainViewModel

private const val TAG = "HomeFragment_싸피"
class HomeFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private var filterLastClickTime: Long = 0
    private var sortLastClickTime: Long = 0

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

        binding.apply {
            (requireContext() as MainActivity).apply {
                setSupportActionBar(toolbar)
                supportActionBar!!.setDisplayShowCustomEnabled(true)
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
                setHasOptionsMenu(true)
        }

        CarListService().getAllCarList(GetAllCarCallback())
        }
    }

    override fun onStart() {
        super.onStart()
        mainActivity.hideBottomNav(false)
    }

    inner class GetAllCarCallback: RetrofitCallback<List<Car>> {
        override fun onSuccess(code: Int, responseData: List<Car>) {
            responseData.let {
                mainViewModel.carList.addAll(responseData)
                carAdapter = CarAdapter(mainActivity, mainViewModel.carList.value!!)

                mainViewModel.carList.observe(viewLifecycleOwner) {
                    carAdapter.datas = it
                    carAdapter.notifyDataSetChanged()
                }

                carAdapter.onItemClickListener = object : CarAdapter.OnItemClickListener {
                    override fun onClick(view: View, position: Int) {
                        var carId = mainViewModel.carList.getItem(position).carId
                        mainActivity.openFragment(1, "carId", carId)
                    }
                }
            }

            binding.recyclerview.apply {
                layoutManager = GridLayoutManager(mainActivity, 2)
                adapter = carAdapter
            }


        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "차 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.car_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when(item.itemId){
            R.id.filter -> {
                val elapsedRealtime = SystemClock.elapsedRealtime()
                if((elapsedRealtime - filterLastClickTime) > 1000){
                    FilterBottomFragment.newInstance().show(
                        parentFragmentManager, FilterBottomFragment.TAG
                    )
                }
                filterLastClickTime = SystemClock.elapsedRealtime()
                true
            }
            R.id.sort -> {
                val elapsedRealtime = SystemClock.elapsedRealtime()
                if((elapsedRealtime - sortLastClickTime) > 1000){
                    SortBottomFragment.newInstance().show(
                        parentFragmentManager, SortBottomFragment.TAG
                    )
                }
                sortLastClickTime = SystemClock.elapsedRealtime()
                true
            }
            else -> false
        }

    }
}
package com.elecguitar.android.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elecguitar.android.R
import com.elecguitar.android.databinding.RecyclerCarItemBinding
import com.elecguitar.android.dto.Car
import com.elecguitar.android.util.CommonUtil
import com.elecguitar.android.util.ListLiveData

private const val TAG = "CarAdapter_싸피"
class CarAdapter(private val context: Context, var datas: MutableList<Car>): RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    lateinit var onItemClickListener : OnItemClickListener

    inner class CarViewHolder(private val binding: RecyclerCarItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Car) {
            binding.apply {
                Glide.with(context)
                    .load(data.img)
                    .into(ivCar)
                tvName.text = data.carName

                tvPrice.text = if (data.price == 0) {
                    "-"
                } else {
                    "${CommonUtil().makeComma(data.price)} 만원"
                }

                tvManufactureDate.text = "${data.manufactureDate} 년"

                tvElecMileage.text = if (data.elecMileage == 0.0f) {
                    "-"
                } else {
                    "${data.elecMileage} km/kWh"
                }

                tvBatteryCapacity.text = if (data.batteryCapacity == 0) {
                    "-"
                } else {
                    "${data.batteryCapacity} Ah"
                }

                if (data.sellStatus == "시판") {
                    chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.elecguitar_yellow))
                    chip.text = "시판"
                } else {
                    chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.elecguitar_g1))
                    chip.text = "단종"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.CarViewHolder {
        return CarViewHolder(RecyclerCarItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.apply {
            bind(datas[position])
            itemView.setOnClickListener {
                onItemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}
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

class CarAdapter(private val context: Context, private val datas: List<Car>): RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

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
                tvPrice.text = "${data.price} 만원"
                tvManufactureDate.text = "${data.manufactureDate} 년"
                tvElecMileage.text = "${data.elecMileage} km/kWh"
                tvBatteryCapacity.text = "${data.batteryCapacity} Ah"

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
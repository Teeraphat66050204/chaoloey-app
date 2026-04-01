package com.example.chaoloey.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaoloey.data.model.Car
import com.example.chaoloey.databinding.ItemCarBinding

class CarAdapter(
    private val cars: MutableList<Car>,
    private val isGridLayout: Boolean = false,
    private val onItemClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(private val binding: ItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            try {
                binding.carNameTextView.text = car.name
                binding.pricePerDayTextView.text = "฿${String.format("%,.0f", car.pricePerDay)}"
                binding.locationTextView.text = "City, Country"
                binding.ratingTextView.text = "5.0"

                Glide.with(binding.carImageView.context)
                    .load(car.imageUrl)
                    .centerCrop()
                    .into(binding.carImageView)

                binding.bookNowButton.setOnClickListener {
                    onItemClick(car)
                }

                binding.root.setOnClickListener {
                    onItemClick(car)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        return try {
            val binding = ItemCarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CarViewHolder(binding)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(cars[position])
    }

    override fun getItemCount(): Int = cars.size

    fun updateData(newCars: List<Car>) {
        cars.clear()
        cars.addAll(newCars)
        notifyDataSetChanged()
    }
}

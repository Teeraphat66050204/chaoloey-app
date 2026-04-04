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
            binding.carNameTextView.text = car.name
            binding.pricePerDayTextView.text = binding.root.context.getString(
                com.example.chaoloey.R.string.price_format,
                String.format(java.util.Locale.US, "%,.0f", car.pricePerDay)
            )
            binding.locationTextView.text = car.category
            binding.ratingTextView.text = car.transmission

            Glide.with(binding.carImageView.context)
                .load(car.imageUrl)
                .centerCrop()
                .into(binding.carImageView)

            binding.bookNowButton.setOnClickListener { onItemClick(car) }
            binding.root.setOnClickListener { onItemClick(car) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
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

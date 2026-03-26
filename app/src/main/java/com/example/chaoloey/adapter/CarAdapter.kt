package com.example.chaoloey.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaoloey.data.model.Car
import com.example.chaoloey.databinding.ItemCarBinding

class CarAdapter(
    private val cars: MutableList<Car>,
    private val onItemClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(private val binding: ItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.carNameTextView.text = car.name
            binding.carMetaTextView.text = "${car.brand} ${car.model}  •  ${car.fuelType}"
            binding.carCategoryTextView.text = car.category.uppercase()
            binding.carPriceTextView.text = "THB ${car.pricePerDay.toInt()}"
            binding.carSpecSeatsTextView.text = "${car.seats} seats"
            binding.carSpecTransmissionTextView.text = car.transmission
            binding.carSpecYearTextView.text = car.year.toString()

            Glide.with(binding.carImageView.context)
                .load(car.imageUrl)
                .centerCrop()
                .into(binding.carImageView)

            binding.root.setOnClickListener {
                onItemClick(car)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
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

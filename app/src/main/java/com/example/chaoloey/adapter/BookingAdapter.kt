package com.example.chaoloey.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaoloey.data.model.BookingItem
import com.example.chaoloey.databinding.ItemBookingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BookingAdapter(
    private val bookings: MutableList<BookingItem>
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookingItem) {
            binding.carNameTextView.text = item.car.name
            binding.dateRangeTextView.text = "${formatDate(item.startDate)}  ->  ${formatDate(item.endDate)}"
            binding.startDateTextView.text = "START ${formatDate(item.startDate)}"
            binding.endDateTextView.text = "END ${formatDate(item.endDate)}"
            binding.totalPriceTextView.text = "THB ${item.totalPrice.toInt()}"
            binding.statusTextView.text = formatStatus(item.status)

            Glide.with(binding.carImageView.context)
                .load(item.car.imageUrl)
                .centerCrop()
                .into(binding.carImageView)

            val (backgroundColor, textColor) = when (item.status.uppercase(Locale.getDefault())) {
                "CONFIRMED" -> Pair(
                    ContextCompat.getColor(binding.root.context, com.example.chaoloey.R.color.chaoloey_success_bg),
                    ContextCompat.getColor(binding.root.context, com.example.chaoloey.R.color.chaoloey_success_text)
                )
                "PENDING" -> Pair(
                    ContextCompat.getColor(binding.root.context, com.example.chaoloey.R.color.chaoloey_warning_bg),
                    ContextCompat.getColor(binding.root.context, com.example.chaoloey.R.color.chaoloey_warning_text)
                )
                else -> Pair(
                    ContextCompat.getColor(binding.root.context, com.example.chaoloey.R.color.chaoloey_primary),
                    ContextCompat.getColor(binding.root.context, com.example.chaoloey.R.color.chaoloey_text)
                )
            }

            binding.statusTextView.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            binding.statusTextView.setTextColor(textColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size

    fun updateData(newBookings: List<BookingItem>) {
        bookings.clear()
        bookings.addAll(newBookings)
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
        )

        for (inputFormat in inputFormats) {
            try {
                val date = inputFormat.parse(dateString)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (_: Exception) {
                // Try the next supported ISO format.
            }
        }

        return dateString
    }

    private fun formatStatus(status: String): String {
        return status.lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

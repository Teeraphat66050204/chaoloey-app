package com.example.chaoloey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaoloey.data.model.BookingItem
import com.example.chaoloey.databinding.ItemBookingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BookingAdapter(
    private val bookings: List<BookingItem>,
    private val onActionClick: (BookingItem) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: BookingItem) {
            binding.carNameTextView.text = booking.car.name

            // Status
            binding.statusChip.text = booking.status
            binding.statusChip.setChipBackgroundColorResource(
                when (booking.status.uppercase()) {
                    "CONFIRMED" -> R.color.booking_confirmed_bg
                    "PENDING"   -> R.color.booking_pending_bg
                    "COMPLETED" -> R.color.booking_completed_bg
                    "CANCELLED" -> R.color.booking_pending_bg
                    else        -> R.color.booking_confirmed_bg
                }
            )

            // แปลงวันจาก ISO string → "dd MMM yyyy"
            val inFmt  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val outFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val startStr = try { outFmt.format(inFmt.parse(booking.startDate)!!) } catch (_: Exception) { booking.startDate }
            val endStr   = try { outFmt.format(inFmt.parse(booking.endDate)!!)   } catch (_: Exception) { booking.endDate }
            binding.dateRangeTextView.text = "$startStr – $endStr"

            // ราคา ฿
            binding.totalPriceTextView.text = "฿${String.format(Locale.US, "%,.0f", booking.totalPrice)}"

            // ซ่อน location (API ไม่มีข้อมูล)
            binding.locationTextView.text = ""

            // รูปรถ
            Glide.with(binding.root.context)
                .load(booking.car.imageUrl)
                .centerCrop()
                .placeholder(R.color.login_input_background)
                .into(binding.carImageView)

            binding.root.setOnClickListener { onActionClick(booking) }
            binding.actionButton.setOnClickListener { onActionClick(booking) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount() = bookings.size
}

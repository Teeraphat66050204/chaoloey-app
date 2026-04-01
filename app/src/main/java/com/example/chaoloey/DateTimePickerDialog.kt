package com.example.chaoloey

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.chaoloey.databinding.DialogDateTimePickerBinding
import java.util.Calendar
import java.util.Locale

class DateTimePickerDialog(
    private val selectedDate: Calendar? = null,
    private val blockedDates: Set<String> = emptySet(),   // "yyyy-MM-dd"
    private val onDateTimeSelected: (Calendar) -> Unit
) : AppCompatDialogFragment() {

    private lateinit var binding: DialogDateTimePickerBinding
    private var currentCalendar: Calendar = Calendar.getInstance()
    private var selectedHour = 10
    private var selectedMinute = 30
    private var amPm = "am"
    private var isCurrentDateBlocked = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDateTimePickerBinding.inflate(layoutInflater)

        selectedDate?.let {
            currentCalendar = it.clone() as Calendar
            selectedHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
            selectedMinute = currentCalendar.get(Calendar.MINUTE)
            amPm = if (selectedHour >= 12) "pm" else "am"
        }

        setupTimeSelection()
        setupCalendar()

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        binding.doneButton.setOnClickListener {
            if (isCurrentDateBlocked) return@setOnClickListener
            val selected = Calendar.getInstance().apply {
                timeInMillis = currentCalendar.timeInMillis
                set(Calendar.HOUR_OF_DAY, if (amPm == "pm" && selectedHour != 12) selectedHour + 12 else if (amPm == "am" && selectedHour == 12) 0 else selectedHour)
                set(Calendar.MINUTE, selectedMinute)
            }
            onDateTimeSelected(selected)
            dialog.dismiss()
        }

        binding.cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun setupTimeSelection() {
        binding.hourPickerEdit.setText(String.format(Locale.US, "%02d", selectedHour % 12).takeIf { it != "00" } ?: "12")
        binding.hourUp.setOnClickListener {
            selectedHour = (selectedHour + 1) % 24
            binding.hourPickerEdit.setText(String.format(Locale.US, "%02d", selectedHour % 12).takeIf { it != "00" } ?: "12")
        }
        binding.hourDown.setOnClickListener {
            selectedHour = if (selectedHour - 1 < 0) 23 else selectedHour - 1
            binding.hourPickerEdit.setText(String.format(Locale.US, "%02d", selectedHour % 12).takeIf { it != "00" } ?: "12")
        }

        binding.minutePickerEdit.setText(String.format(Locale.US, "%02d", selectedMinute))
        binding.minuteUp.setOnClickListener {
            selectedMinute = (selectedMinute + 30) % 60
            binding.minutePickerEdit.setText(String.format(Locale.US, "%02d", selectedMinute))
        }
        binding.minuteDown.setOnClickListener {
            selectedMinute = if (selectedMinute - 30 < 0) 30 else selectedMinute - 30
            binding.minutePickerEdit.setText(String.format(Locale.US, "%02d", selectedMinute))
        }

        binding.amButton.isSelected = amPm == "am"
        binding.pmButton.isSelected = amPm == "pm"
        binding.amButton.setOnClickListener {
            amPm = "am"; binding.amButton.isSelected = true; binding.pmButton.isSelected = false
        }
        binding.pmButton.setOnClickListener {
            amPm = "pm"; binding.pmButton.isSelected = true; binding.amButton.isSelected = false
        }
    }

    private fun setupCalendar() {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        binding.calendarView.minDate = tomorrow.timeInMillis
        binding.calendarView.setDate(currentCalendar.timeInMillis.coerceAtLeast(tomorrow.timeInMillis), false, false)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val key = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            if (blockedDates.contains(key)) {
                binding.calendarView.setDate(currentCalendar.timeInMillis, false, false)
                binding.tvBlockedWarning.visibility = View.VISIBLE
                binding.doneButton.isEnabled = false
                isCurrentDateBlocked = true
            } else {
                binding.tvBlockedWarning.visibility = View.GONE
                binding.doneButton.isEnabled = true
                isCurrentDateBlocked = false
                currentCalendar.set(year, month, dayOfMonth)
            }
        }
    }
}



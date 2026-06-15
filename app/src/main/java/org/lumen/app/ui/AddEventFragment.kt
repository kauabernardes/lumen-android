package org.lumen.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.CreateEventRequest
import org.lumen.app.databinding.FragmentAddEventBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0
    private var selectedHour = 0
    private var selectedMinute = 0

    private var isDateSelected = false
    private var isTimeSelected = false

    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            handleSaveEvent()
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.etTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->

            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            binding.etDate.setText(formatter.format(Date(selection)))

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            selectedYear = calendar.get(Calendar.YEAR)
            selectedMonth = calendar.get(Calendar.MONTH) + 1
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

            isDateSelected = true
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG")
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Selecione a hora")
            .build()

        timePicker.addOnPositiveButtonClickListener {

            val formattedTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
            binding.etTime.setText(formattedTime)

            selectedHour = timePicker.hour
            selectedMinute = timePicker.minute

            isTimeSelected = true
        }

        timePicker.show(parentFragmentManager, "TIME_PICKER_TAG")
    }

    private fun handleSaveEvent() {
        val title = binding.etTitle.text.toString()
        val desc = binding.etDescription.text.toString()

        if (title.isEmpty()) {
            showBottomSheet(message = getString(R.string.fill_event_title))
            return
        }

        if (!isDateSelected) {
            showBottomSheet(message = getString(R.string.fill_event_date))
            return
        }

        if (!isTimeSelected) {
            showBottomSheet(message = getString(R.string.fill_event_time))
            return
        }

        val localDate = LocalDate.of(selectedYear, selectedMonth, selectedDay)
        val localTime = LocalTime.of(selectedHour, selectedMinute)

        val zonedDateTime = ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault())
        val iso8601DateTime = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Ativa o estado de carregamento e bloqueia as views
                setLoadingState(true)

                val request = CreateEventRequest(title, desc, iso8601DateTime)
                val response = RetrofitClient.agendaApi.create(tokenManager.getBearer(), request)

                if (response.isSuccessful) {
                    findNavController().popBackStack()
                } else {
                    showBottomSheet(message = response.errorMessage())
                }

            } catch (e: Exception) {
                showBottomSheet(message = e.message ?: "Erro ao salvar evento")
            } finally {

                if (_binding != null) {
                    setLoadingState(false)
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading

        binding.btnSave.isEnabled = !isLoading
        binding.btnCancel.isEnabled = !isLoading
        binding.etTitle.isEnabled = !isLoading
        binding.etDescription.isEnabled = !isLoading
        binding.etDate.isEnabled = !isLoading
        binding.etTime.isEnabled = !isLoading

        binding.btnSave.text = if (isLoading) "Salvando..." else "Salvar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
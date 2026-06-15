package org.lumen.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.AchievedGoal
import org.lumen.app.data.remote.model.CreateDailyRequest
import org.lumen.app.databinding.FragmentDailyBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet

class DailyFragment : Fragment() {
    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentDailyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCheckin.setOnClickListener {
            handleCheckin()
        }
    }




    private fun handleCheckin() {
        val studiedYesterday = binding.etStudiedYesterday.text.toString().trim()
        val studyToday = binding.etStudyToday.text.toString().trim()

        val goalReached = when (binding.rgGoalReached.checkedRadioButtonId) {
            R.id.rb_yes -> AchievedGoal.SIM
            R.id.rb_no -> AchievedGoal.NAO
            R.id.rb_almost -> AchievedGoal.QUASE
            else -> null
        }

        val mood = when (binding.rgMood.checkedRadioButtonId) {
            R.id.rb_mood_bad -> "1"
            R.id.rb_mood_ok -> "3"
            R.id.rb_mood_good -> "2"
            else -> null
        }


        if (studiedYesterday.isEmpty() || studyToday.isEmpty()) {
            showBottomSheet(message = "Preencha os campos de texto para continuar.")
            return
        }

        if (goalReached == null || mood == null) {
            showBottomSheet(message = "Por favor, selecione as opções de meta e humor.")
            return
        }

        // Tudo preenchido, inicia a requisição
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                setLoadingState(true)


                val request = CreateDailyRequest(
                    studiedYesterday = studiedYesterday,
                    achievedGoal = goalReached,
                    mood = mood,
                    studyToday = studyToday
                )

                val response = RetrofitClient.dailyApi.create(tokenManager.getBearer(), request)

                if (response.isSuccessful) {
                    showBottomSheet(message = "Check-in diário realizado com sucesso!")
                    findNavController().popBackStack()
                } else {
                    showBottomSheet(message = response.errorMessage())
                }

            } catch (e: Exception) {
                Log.e("DailyFragment", "Erro ao fazer check-in", e)
                showBottomSheet(message = e.message ?: "Erro de conexão. Tente novamente.")
            } finally {
                if (_binding != null) {
                    setLoadingState(false)
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnCheckin.isEnabled = !isLoading
        binding.etStudiedYesterday.isEnabled = !isLoading
        binding.etStudyToday.isEnabled = !isLoading
        for (i in 0 until binding.rgGoalReached.childCount) {
            binding.rgGoalReached.getChildAt(i).isEnabled = !isLoading
        }
        for (i in 0 until binding.rgMood.childCount) {
            binding.rgMood.getChildAt(i).isEnabled = !isLoading
        }

        binding.btnCheckin.text = if (isLoading) "Enviando..." else getString(R.string.daily_btn_checkin)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package org.lumen.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.RecommendationResponse
import org.lumen.app.databinding.FragmentHomeBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private lateinit var username: String

    private var recommendation: RecommendationResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        tokenManager = TokenManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        username = tokenManager.getUsername()

        initListener()
        loadUser()

        if (recommendation != null) {
            binding.recommendationTitle.text = recommendation!!.title
            binding.recommendationSubtitle.text = recommendation!!.subtitle
            binding.progressBar.isVisible = false
        } else {
            loadRecommendation()
        }
    }

    private fun loadUser() {
        binding.helloTitle.text = "Olá $username"
    }

    private fun initListener() {
        binding.cardSession.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sessionFragment)
        }

        binding.cardCommunity.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_explorarFragment)
        }

        binding.cardCheckin.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dailyFragment)
        }

        binding.cardAchievements.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_conquistasFragment)
        }
    }

    private fun loadRecommendation() {
        binding.progressBar.isVisible = true
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response =
                    RetrofitClient.recommendationApi.recommendation(tokenManager.getBearer())

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    recommendation = data

                    binding.recommendationTitle.text = data.title
                    binding.recommendationSubtitle.text = data.subtitle
                } else {
                    showBottomSheet(message = response.errorMessage())
                }
                binding.progressBar.isVisible = false
            } catch (e: Exception) {
                showBottomSheet(message = e.message!!)
                binding.progressBar.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

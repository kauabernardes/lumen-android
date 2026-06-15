package org.lumen.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.lumen.app.adapter.ConquistaAdapter
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.model.Reward
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentConquistasBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet

class ConquistasFragment : Fragment() {
    private var _binding: FragmentConquistasBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private val listReward = mutableListOf<Reward>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentConquistasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        load()
    }

    private fun load() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.rewardApi.my(tokenManager.getBearer())

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    listReward.clear()
                    listReward.addAll(data)
                    val adapter = ConquistaAdapter(listReward)
                    binding.recyclerConquistas.adapter = adapter
                } else {
                    showBottomSheet(message = response.errorMessage())
                }
            } catch (e: Exception) {
                showBottomSheet(message = e.message!!)
            }
        }

        val adapter = ConquistaAdapter(listReward)
        binding.recyclerConquistas.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
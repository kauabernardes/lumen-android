package org.lumen.app.ui.auth

import TokenManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.adapter.CommunityVerticalAdapter
import org.lumen.app.data.model.Community
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentProfileBinding
import org.lumen.app.util.errorMessage


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.name.text = tokenManager.getUsername()
        binding.username.text = "@${tokenManager.getUsername()}"
        carregarComunidades()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun carregarComunidades(){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("TESTE12345", "laslaosa")
                val response = RetrofitClient.communityApi.imIn(tokenManager.getBearer().toString())

                if (response.isSuccessful && response.body() != null) {
                    val communities = response.body()!!
                    Log.d("TESTE12345", communities.toString())
                    renderComunidade(communities)
                } else {
                    val eMsg = response.errorMessage()
                    Log.d("TESTE12345", eMsg)
                }


            } catch (e: Exception) {

            }
        }
    }

    private fun renderComunidade(communities: List<Community>) {
        val communityAdapter = CommunityVerticalAdapter(communities) {
                communityClicked ->
            Log.d("COMUNIDADE", communityClicked.description.toString())
            val action = ProfileFragmentDirections.actionProfileFragmentToFeedComunidadeFragment(communityClicked.id)
            findNavController().navigate(action)
        }
        binding.recyclerCommunities.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCommunities.setHasFixedSize(true)
        binding.recyclerCommunities.adapter = communityAdapter
    }


}
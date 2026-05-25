package org.lumen.app.ui

import TokenManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.auth0.jwt.JWT
import org.lumen.app.R
import org.lumen.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private lateinit var username : String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        tokenManager = TokenManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        username = tokenManager.getUsername()

        initListener()
        loadUser()
    }

    private fun loadUser() {
        binding.helloTitle.text = "Olá $username"
    }

    private fun initListener() {
        binding.cardSession.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sessionFragment)
        }
        binding.cardCommunity.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
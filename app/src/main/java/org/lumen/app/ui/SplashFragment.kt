package org.lumen.app.ui

import TokenManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.jwt.JWT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.databinding.FragmentSplashBinding
import java.util.Date

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        viewLifecycleOwner.lifecycleScope.launch {
            delay(4000)

            val token = tokenManager.getToken()

            if (!token.isNullOrEmpty()) {
                try {
                    val decodedJwt = JWT.decode(token)

                    if (decodedJwt.expiresAt.before(Date())) {
                        tokenManager.clear()
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }
                } catch (e: Exception) {
                    tokenManager.clear()
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
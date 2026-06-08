package org.lumen.app.ui.comunidade

import org.lumen.app.data.local.TokenManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.lumen.app.databinding.FragmentExplorarComunidadesBinding

class ExplorarFragment : Fragment() {

    private var _binding : FragmentExplorarComunidadesBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentExplorarComunidadesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
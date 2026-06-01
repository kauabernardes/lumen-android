package org.lumen.app.ui

import TokenManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.lumen.app.R
import org.lumen.app.databinding.FragmentFeedcomunidadesBinding



class FeedComunidadeFragment : Fragment() {

    private var _binding: FragmentFeedcomunidadesBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())

        _binding = FragmentFeedcomunidadesBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_feedcomunidades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
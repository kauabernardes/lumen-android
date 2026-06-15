package org.lumen.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.CreatePostRequest
import org.lumen.app.databinding.FragmentCriarnovopostBinding
import org.lumen.app.util.showBottomSheet

class CriarNovoPost : Fragment() {
    private var _binding: FragmentCriarnovopostBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

    private val args: CriarNovoPostArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentCriarnovopostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.btnPublicar.setOnClickListener {
            val content = binding.etDescricao.text.toString()
            val communityId = args.communityId

            val request = CreatePostRequest(content, communityId)

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response =
                        RetrofitClient.postApi.createPost(tokenManager.getBearer(), request)

                    if (response.isSuccessful) {
                        showBottomSheet(message = "Post criado com sucesso!")
                    }
                    
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

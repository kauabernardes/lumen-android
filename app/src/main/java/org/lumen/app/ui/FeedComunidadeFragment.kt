package org.lumen.app.ui

import TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.adapter.PostAdapter
import org.lumen.app.data.model.post.ClickElement
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.Meta
import org.lumen.app.databinding.FragmentFeedcomunidadesBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.formatDate
import org.lumen.app.util.setupPostLikeInteraction // Mantido o import da função utilitária
import org.lumen.app.util.showBottomSheet

class FeedComunidadeFragment : Fragment() {

    private var _binding: FragmentFeedcomunidadesBinding? = null
    private val binding get() = _binding!!

    private val args: FeedComunidadeFragmentArgs by navArgs()
    private lateinit var tokenManager: TokenManager

    private lateinit var meta: Meta

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentFeedcomunidadesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCommunityDetails()
        loadPosts()
    }

    private fun loadCommunityDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.communityApi.community(tokenManager.getBearer(), args.communityId)

                if (response.isSuccessful && response.body() != null) {
                    val community = response.body()!!
                    binding.tvNomeComunidade.text = community.name
                    binding.tvDescricaoComunidade.text = community.description
                    binding.tvCriadoPor.text = "Criado por: ${community.author.username}"
                    binding.tvDataCriacao.text = "Em ${formatDate(community.createdAt.toString())}"
                } else {
                    showBottomSheet(message = response.errorMessage())
                }
            } catch (e: Exception) {
                showBottomSheet(message = getString(R.string.error_default))
            }
        }
    }

    private fun loadPosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.communityApi.communityPosts(tokenManager.getBearer(), args.communityId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val posts = body.data

                    meta = body.meta

                    lateinit var postsAdapter: PostAdapter

                    postsAdapter = PostAdapter(posts) { postClicked, position , element ->
                        if (element == ClickElement.LIKE) {
                            setupPostLikeInteraction(postClicked, position, postsAdapter, tokenManager.getBearer())
                        }
                        if (element == ClickElement.CONTENT) {
                            val action = FeedComunidadeFragmentDirections.actionFeedComunidadeFragmentToPostFragment(postClicked.id)
                            findNavController().navigate(action)
                        }

                    }

                    binding.recyclerPosts.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerPosts.adapter = postsAdapter
                } else {
                    showBottomSheet(message = response.errorMessage())
                }
            } catch (e: Exception) {
                showBottomSheet(message = getString(R.string.error_default))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
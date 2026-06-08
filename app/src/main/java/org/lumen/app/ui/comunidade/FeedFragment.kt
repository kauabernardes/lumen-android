package org.lumen.app.ui.comunidade

import org.lumen.app.data.local.TokenManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.adapter.PostAdapter
import org.lumen.app.data.model.post.ClickElement
import org.lumen.app.data.model.post.Post
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentFeedcomunidadesBinding

import org.lumen.app.util.errorMessage
import org.lumen.app.util.formatDate
import org.lumen.app.util.setupPostLikeInteraction
import org.lumen.app.util.showBottomSheet

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedcomunidadesBinding? = null
    private val binding get() = _binding!!

    private val args: FeedFragmentArgs by navArgs()
    private lateinit var tokenManager: TokenManager

    private var currentPage = 1
    private var isLoading = false
    private var hasMorePages = true

    private val postsSalvos = mutableListOf<Post>()

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

        if (postsSalvos.isNotEmpty()) {
            configurarRecyclerViewInicial(postsSalvos)
            setupScrollListener()
        } else {
            loadPosts()
        }
    }

    private fun loadCommunityDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.communityApi.community(tokenManager.getBearer(), args.communityId)

                if (response.isSuccessful && response.body() != null) {
                    val community = response.body()!!
                    binding.tvNomeComunidade.text = community.name
                    binding.tvDescricaoComunidade.text = community.description
                    binding.tvCriadoPor.text = buildString {
                        append(getString(R.string.default_created_by))
                        append(community.author.username)
                    }
                    binding.tvDataCriacao.text = buildString {
                        append(getString(R.string.default_at))
                        append(formatDate(community.createdAt.toString()))
                    }
                } else {
                    showBottomSheet(message = response.errorMessage())
                }
            } catch (e: Exception) {
                showBottomSheet(message = getString(R.string.error_default))
            }
        }
    }

    private fun loadPosts() {
        if (isLoading || !hasMorePages) return

        isLoading = true
        binding.spinner.isVisible = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.communityApi.communityPosts(
                    tokenManager.getBearer(),
                    args.communityId,
                    page = currentPage,
                    limit = 5
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val posts = body.data
                    hasMorePages = posts.isNotEmpty()

                    if (currentPage == 1) {
                        postsSalvos.clear()
                        postsSalvos.addAll(posts)

                        configurarRecyclerViewInicial(postsSalvos)
                        setupScrollListener()
                    } else {
                        postsSalvos.addAll(posts)


                        (binding.recyclerPosts.adapter as? PostAdapter)?.addPosts(posts)
                    }

                    if (hasMorePages) currentPage++

                } else {
                    showBottomSheet(message = response.errorMessage())
                }
            } catch (e: Exception) {
                showBottomSheet(message = getString(R.string.error_default))
            } finally {
                isLoading = false
                binding.spinner.isGone = true
            }
        }
    }

    private fun configurarRecyclerViewInicial(lista: List<Post>) {
        val adapter = PostAdapter(lista.toMutableList()) { postClicked, position, element ->
            if (element == ClickElement.LIKE) {
                val currentAdapter = binding.recyclerPosts.adapter as PostAdapter
                setupPostLikeInteraction(
                    postClicked,
                    position,
                    currentAdapter,
                    tokenManager.getBearer()
                )
            }
            if (element == ClickElement.CONTENT) {
                val action =
                    FeedFragmentDirections.Companion.actionFeedComunidadeFragmentToPostFragment(
                        postClicked.id
                    )
                findNavController().navigate(action)
            }
        }

        binding.recyclerPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPosts.adapter = adapter
    }

    private fun setupScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            val totalContentHeight = v.getChildAt(0).measuredHeight
            val scrollViewHeight = v.measuredHeight

            if (scrollY >= (totalContentHeight - scrollViewHeight - 300)) {
                if (!isLoading && hasMorePages) {
                    loadPosts()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
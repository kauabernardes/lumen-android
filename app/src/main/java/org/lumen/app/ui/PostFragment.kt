package org.lumen.app.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.adapter.PostAdapter
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.model.post.ClickElement
import org.lumen.app.data.model.post.Post
import org.lumen.app.data.remote.Constants.BASE_URL
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentPostBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.formatDate
import org.lumen.app.util.setupPostLikeInteraction
import org.lumen.app.util.showBottomSheet

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private val args: PostFragmentArgs by navArgs()

    private lateinit var post: Post

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        loadPost()
    }

    private fun loadPost() {
        binding.titleComments.isVisible = false
        binding.emptyComments.isVisible = false
        binding.post.isVisible = false
        binding.recyclerComments.isVisible = false
        binding.spinnerLoading.isVisible = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.postApi.post(tokenManager.getBearer(), args.postId)

                if (response.isSuccessful && response.body() != null) {
                    post = response.body()!!

                    renderParent(post)
                    renderComments(post.comments!!)
                } else {
                    showBottomSheet(message = response.errorMessage())
                }

                binding.titleComments.isVisible = true
                binding.emptyComments.isVisible = true
                binding.post.isVisible = true
                binding.recyclerComments.isVisible = true
                binding.spinnerLoading.isVisible = false
            } catch (e: Exception) {
            }
        }
    }

    private fun renderParent(post: Post) {
        binding.content.text = post.content
        binding.username.text = post.user.username
        binding.subUsername.text = "@${post.user.username}"
        binding.communityName.text = post.community?.name
        binding.valueLike.text = post.likesCount.toString()
        binding.valueComments.text = post.commentsCount.toString()
        binding.date.text = formatDate(post.createdAt.toString())

        if (post.parent != null) {
            binding.answerOf.text =
                "Em resposta a @${post.parent?.user?.username}"
        }

        Log.e("click", post.user.profileImage)

        Glide
            .with(requireContext())
            .load("${BASE_URL}uploads/${post.user.profileImage}")
            .placeholder(R.drawable.ic_user_circle)
            .into(binding.avatar)

        binding.communityName.setOnClickListener {
            if (post.community != null) {
                val action =
                    PostFragmentDirections.actionPostFragmentToFeedComunidadeFragment(post.community.id)
                findNavController().navigate(action)
            }
        }

        if (post.isLiked == true) {
            binding.iconLike.setImageResource(R.drawable.ic_heart_filled)
            binding.iconLike.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.card_checkin_text,
                    ),
                )
        } else {
            binding.iconLike.setImageResource(R.drawable.ic_heart)
            binding.iconLike.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_secondary,
                    ),
                )
        }
        binding.iconLike.setOnClickListener { handleLike() }
    }

    private fun renderComments(posts: MutableList<Post>) {
        binding.emptyComments.isVisible = posts.isEmpty()

        lateinit var postsAdapter: PostAdapter
        postsAdapter =
            PostAdapter(posts) { postClicked, position, element ->

                if (element == ClickElement.LIKE) {
                    setupPostLikeInteraction(
                        postClicked,
                        position,
                        postsAdapter,
                        tokenManager.getBearer(),
                    )
                }
                if (element == ClickElement.CONTENT || element == ClickElement.COMMENT) {
                    val action = PostFragmentDirections.actionPostFragmentSelf(postClicked.id)
                    findNavController().navigate(action)
                }
                if (element == ClickElement.USER) {
                    val action =
                        PostFragmentDirections.actionPostFragmentToProfileFragment(postClicked.user.id)
                    findNavController().navigate(action)
                }
            }

        val recycler = binding.recyclerComments

        recycler.layoutManager = LinearLayoutManager(requireContext())
        val dividirItemDecoration =
            MaterialDividerItemDecoration(
                recycler.context,
                LinearLayoutManager.VERTICAL,
            )
        recycler.addItemDecoration(dividirItemDecoration)
        recycler.adapter = postsAdapter
    }

    private fun handleLike() {
        previewLike()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.postApi.like(tokenManager.getBearer(), post.id)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    post.isLiked = body.liked
                    post.likesCount = body.totalLikes

                    if (post.isLiked == true) {
                        post.likesCount = post.likesCount
                        binding.iconLike.setImageResource(R.drawable.ic_heart_filled)
                        binding.iconLike.imageTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.card_checkin_text,
                                ),
                            )
                    } else {
                        post.likesCount = post.likesCount
                        binding.iconLike.setImageResource(R.drawable.ic_heart)
                        binding.iconLike.imageTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.text_secondary,
                                ),
                            )
                    }
                } else {
                    showBottomSheet(message = response.errorMessage())
                }
            } catch (e: Exception) {
                previewLike()
                showBottomSheet(message = "Sem conexão com o servidor.")
            }
        }
    }

    fun previewLike() {
        post.isLiked = post.isLiked != true
        if (post.isLiked == true) {
            post.likesCount = post.likesCount?.plus(1)
            binding.iconLike.setImageResource(R.drawable.ic_heart_filled)
        } else {
            post.likesCount = post.likesCount?.minus(1)
            binding.iconLike.setImageResource(R.drawable.ic_heart)
        }
        binding.valueLike.text = post.likesCount.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

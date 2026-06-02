package org.lumen.app.ui

import TokenManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.adapter.PostAdapter
import org.lumen.app.data.model.post.ClickElement
import org.lumen.app.data.model.post.Post
import org.lumen.app.data.model.post.PostUser
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentPostBinding
import org.lumen.app.util.formatDate
import org.lumen.app.util.setupPostLikeInteraction

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager : TokenManager

    private val args : PostFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPost()

    }

    private fun loadPost() {

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val response = RetrofitClient.postApi.post(tokenManager.getBearer(), args.postId)

                if (response.isSuccessful && response.body() != null) {

                    val data = response.body()!!
                    renderParent(data)
                    renderComments(data.comments!!)

                    Log.d("POSTAGEM", data.toString())
                }


            }
            catch (e: Exception) {

            }
        }

    }

    private fun renderParent (post: Post) {
        binding.content.text = post.content
        binding.username.text = post.user.username
        binding.subUsername.text = "@${post.user.username}"
        binding.valueLike.text = post.likesCount.toString()
        binding.valueComments.text = post.commentsCount.toString()
        binding.date.text = formatDate(post.createdAt.toString())

    }

    private fun renderComments(posts: List<Post>) {
        lateinit var postsAdapter: PostAdapter
        postsAdapter = PostAdapter(posts) {
            postClicked, position, element ->

            if (element == ClickElement.LIKE) {
                setupPostLikeInteraction(postClicked, position, postsAdapter, tokenManager.getBearer())
            }
            if (element == ClickElement.CONTENT) {
                val action = PostFragmentDirections.actionPostFragmentSelf(postClicked.id)
                findNavController().navigate(action)
            }


        }

        binding.recyclerComments.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerComments.adapter = postsAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
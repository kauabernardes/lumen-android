package org.lumen.app.ui.auth

import org.lumen.app.data.local.TokenManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.lumen.app.adapter.CommunityVerticalAdapter
import org.lumen.app.data.model.Community
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private var currentPage = 1
    private var isLoading = false
    private var hasMorePages = true

    private val comunidadesSalvas = mutableListOf<Community>()


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
        Glide.with(this)
            .load(tokenManager.getProfileImage())
            .into(binding.userIcon)


        if (comunidadesSalvas.isNotEmpty()) {

            configurarRecyclerViewInicial(comunidadesSalvas)
            setupScrollListener()
        } else {

            carregarComunidades()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun carregarComunidades() {
        if (isLoading || !hasMorePages) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.communityApi.imIn(tokenManager.getBearer(), page = currentPage)

                if (response.isSuccessful && response.body() != null) {
                    val communitiesList = response.body()!!.data
                    hasMorePages = communitiesList.isNotEmpty()

                    if (currentPage == 1) {
                        comunidadesSalvas.clear()
                        comunidadesSalvas.addAll(communitiesList)
                        configurarRecyclerViewInicial(comunidadesSalvas)
                        setupScrollListener()
                    } else {
                        comunidadesSalvas.addAll(communitiesList)
                        (binding.recyclerCommunities.adapter as? CommunityVerticalAdapter)?.addCommunities(communitiesList)
                    }

                    if (hasMorePages) currentPage++
                }
            } catch (e: Exception) {
                Log.e("ERRO", "Falha", e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun configurarRecyclerViewInicial(lista: List<Community>) {
        val mutableCommunities = lista.toMutableList()

        val communityAdapter = CommunityVerticalAdapter(mutableCommunities) {
            communityClicked ->
                val action = ProfileFragmentDirections.actionProfileFragmentToFeedComunidadeFragment(communityClicked.id)
                findNavController().navigate(action)
        }

        binding.recyclerCommunities.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCommunities.adapter = communityAdapter
    }

    private fun setupScrollListener() {
        binding.recyclerCommunities.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dx > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && hasMorePages) {

                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                            carregarComunidades()
                        }
                    }
                }
            }
        })
    }


}
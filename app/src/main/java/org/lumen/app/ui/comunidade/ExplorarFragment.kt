package org.lumen.app.ui.comunidade

import org.lumen.app.data.local.TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.lumen.app.adapter.CommunityVerticalAdapter
import org.lumen.app.data.model.Community
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentExplorarComunidadesBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet

class ExplorarFragment : Fragment() {

    private var _binding : FragmentExplorarComunidadesBinding? = null
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
        _binding = FragmentExplorarComunidadesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (comunidadesSalvas.isNotEmpty()) {
            initList(comunidadesSalvas)
            setupScrollListener()

            binding.comuMainName.text = comunidadesSalvas.firstOrNull()?.name ?: ""
        } else {
            load()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun load() {
        if (isLoading || !hasMorePages) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val response = RetrofitClient.communityApi.imNotIn(tokenManager.getBearer(), page = currentPage)

                if (response.isSuccessful && response.body() != null) {
                    val communities = response.body()!!.data
                    hasMorePages = communities.isNotEmpty()

                    if (currentPage == 1) {
                        comunidadesSalvas.clear()
                        comunidadesSalvas.addAll(communities)


                        if (communities.isNotEmpty()) {
                            val first = communities[0]
                            binding.comuMainName.text = first.name
                        }

                        initList(comunidadesSalvas)
                        setupScrollListener()
                    } else {
                        comunidadesSalvas.addAll(communities)

                        (binding.comuRecycler.adapter as? CommunityVerticalAdapter)?.addCommunities(communities)
                    }

                    if (hasMorePages) currentPage++

                } else {
                    val eMsg = response.errorMessage()
                    showBottomSheet(message = eMsg)
                }

            } catch (e: Exception) {
                Log.e("ExplorarFragment", "Erro ao carregar comunidades", e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun initList(communities: List<Community>){
        val mutableCommunities = communities.toMutableList()
        val communityAdapter = CommunityVerticalAdapter(mutableCommunities, true) { communityClicked ->

        }

        binding.comuRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.comuRecycler.adapter = communityAdapter
    }

    private fun setupScrollListener() {
        binding.comuRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && hasMorePages) {

                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                            load()
                        }
                    }
                }
            }
        })
    }
}
package org.lumen.app.ui.comunidade

import android.graphics.Color
import org.lumen.app.data.local.TokenManager
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.adapter.CommunityVerticalAdapter
import org.lumen.app.data.model.Community
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentExplorarComunidadesBinding
import org.lumen.app.util.EnumTabComunidade
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

    private var currentMode = EnumTabComunidade.EXPLORAR

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

        setupTabs()

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

    private fun setupTabs() {
        binding.tabMinhas.setOnClickListener {
            if (currentMode != EnumTabComunidade.MINHAS) {
                switchTab(EnumTabComunidade.MINHAS)
            }
        }

        binding.tabExplorar.setOnClickListener {
            if (currentMode != EnumTabComunidade.EXPLORAR) {
                switchTab(EnumTabComunidade.EXPLORAR)
            }
        }
    }

    private fun switchTab(newMode: EnumTabComunidade) {
        currentMode = newMode

        if (newMode == EnumTabComunidade.MINHAS) {
            binding.tabMinhas.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tabMinhas.setTextColor(ContextCompat.getColor(requireContext(), R.color.card_community_text))

            binding.tabExplorar.background = null
            binding.tabExplorar.setTextColor(Color.parseColor("#666666"))
        } else {
            binding.tabExplorar.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tabExplorar.setTextColor(ContextCompat.getColor(requireContext(), R.color.card_community_text))

            binding.tabMinhas.background = null
            binding.tabMinhas.setTextColor(Color.parseColor("#666666"))
        }

        currentPage = 1
        hasMorePages = true
        comunidadesSalvas.clear()
        binding.comuRecycler.adapter = null

        load()
    }

    private fun load() {
        if (isLoading || !hasMorePages) return
        isLoading = true

        if (currentPage == 1) {
            binding.progressBar.isVisible = true
            binding.cardComuMain.isVisible = false
            binding.comuRecycler.isVisible = false
            binding.textAlert.isVisible = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = if (currentMode == EnumTabComunidade.EXPLORAR) {
                    RetrofitClient.communityApi.imNotIn(tokenManager.getBearer(), page = currentPage)
                } else {
                    RetrofitClient.communityApi.imIn(tokenManager.getBearer(), page = currentPage)
                }

                if (response.isSuccessful && response.body() != null) {
                    val communities = response.body()!!.data
                    hasMorePages = communities.isNotEmpty()

                    val meta = response.body()!!.meta

                    if (meta.total == 0) {
                        binding.cardComuMain.isVisible = false
                        binding.textAlert.isVisible = true
                        binding.textAlert.text = getString(R.string.empty_community)
                    } else {
                        binding.cardComuMain.isVisible = true
                        binding.textAlert.isVisible = false
                        binding.comuRecycler.isVisible = true
                    }

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
                showBottomSheet(message = getString(R.string.error_default))
            } finally {
                isLoading = false
                // Esconde o ProgressBar independente de sucesso ou falha
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun initList(communities: List<Community>){
        val mutableCommunities = communities.toMutableList()
        val communityAdapter = CommunityVerticalAdapter(mutableCommunities, true) { communityClicked ->
            if (communityClicked.isMember == false) {
                viewLifecycleOwner.lifecycleScope.launch {
                    try{

                        val response = RetrofitClient.communityApi.joinCommunity(tokenManager.getBearer(), communityClicked.id)

                        if (response.isSuccessful && response.body() != null){
                            val body = response.body()!!
                            showBottomSheet(message = body.message)

                            val action = ExplorarFragmentDirections.actionExplorarFragmentToFeedComunidadeFragment(communityClicked.id)
                            findNavController().navigate(action)

                        }


                    } catch (e: Exception) {
                        showBottomSheet(message = getString(R.string.error_default))
                    }
                }
            } else {
                val action = ExplorarFragmentDirections.actionExplorarFragmentToFeedComunidadeFragment(communityClicked.id)
                findNavController().navigate(action)
            }
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
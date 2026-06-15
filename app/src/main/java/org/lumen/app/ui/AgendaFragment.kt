package org.lumen.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.adapter.EventAdapter
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.model.Event
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentAgendaBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet

class AgendaFragment : Fragment() {

    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private lateinit var adapter: EventAdapter

    private val eventList = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load()
        setupListeners()
    }

    private fun setupListeners() {
        binding.fabAddEvent.setOnClickListener {
            findNavController().navigate(R.id.action_agendaFragment_to_addEventFragment)
        }
    }

    private fun load(){

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val response = RetrofitClient.agendaApi.my(tokenManager.getBearer())

                if (response.isSuccessful && response.body() != null) {

                    Log.d("cooaksoasa", response.body().toString())

                    eventList.clear()
                    eventList.addAll(response.body()!!)

                    val adapter = EventAdapter(eventList) {}

                    binding.rvEvents.adapter = adapter

                } else {
                    showBottomSheet(message = response.errorMessage())
                }



            } catch (e: Exception) {

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package org.lumen.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.lumen.app.R
import org.lumen.app.adapter.EventAdapter
import org.lumen.app.data.model.Event

class AgendaFragment : Fragment() {

    // Lista estática compartilhada em memória simples para testes
    companion object {
        val eventList = mutableListOf<Event>(
            Event("Prova de História", "10/10/2025", "08:00", "Conteúdo do bimestre"),
            Event("Entrega: Trabalho de Química", "18/10/2025", "", "Enviar por e-mail")
        )
    }

    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)

        val rvEvents = view.findViewById<RecyclerView>(R.id.rvEvents)
        val fabAddEvent = view.findViewById<FloatingActionButton>(R.id.fabAddEvent)

        // Configura o Adaptador e Ação de Deletar
        adapter = EventAdapter(eventList) { event ->
            eventList.remove(event)
            adapter.notifyDataSetChanged()
        }
        rvEvents.adapter = adapter

        // Ir para a tela de Adicionar Evento
        fabAddEvent.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, AddEventFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
package org.lumen.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import org.lumen.app.R
import org.lumen.app.data.model.Event

class AddEventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_event, container, false)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etDate = view.findViewById<EditText>(R.id.etDate)
        val etTime = view.findViewById<EditText>(R.id.etTime)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDescription.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (title.isNotEmpty() && date.isNotEmpty()) {
                // Adiciona o novo evento na lista estática
                AgendaFragment.eventList.add(Event(title, date, time, desc))
                parentFragmentManager.popBackStack()
            }
        }

        return view
    }
}
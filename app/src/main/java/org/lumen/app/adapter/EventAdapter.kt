package org.lumen.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lumen.app.R
import org.lumen.app.data.model.Event

class EventAdapter(
    private val events: MutableList<Event>,
    private val onDeleteClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtHeader: TextView = view.findViewById(R.id.txtEventHeader)
        val txtDesc: TextView = view.findViewById(R.id.txtEventDescription)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        val headerText = if (event.time.isNotEmpty()) "${event.title} - ${event.date} às ${event.time}" else "${event.title} - ${event.date}"
        holder.txtHeader.text = headerText
        holder.txtDesc.text = event.description

        holder.btnDelete.setOnClickListener {
            onDeleteClick(event)
        }
    }

    override fun getItemCount(): Int = events.size
}
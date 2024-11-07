package com.example.festapp_ofc

import Evento
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventoAdapter(private val eventoList: List<Evento>) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventoList[position]
        holder.bind(evento)
    }

    override fun getItemCount(): Int = eventoList.size

    inner class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeEvento: TextView = itemView.findViewById(R.id.textViewNomeEvento)
        private val bairroEvento: TextView = itemView.findViewById(R.id.textViewBairroEvento)

        fun bind(evento: Evento) {
            nomeEvento.text = evento.nome
            bairroEvento.text = evento.bairro
        }
    }
}

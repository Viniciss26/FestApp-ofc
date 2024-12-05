package com.example.festapp_ofc

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.festapp_ofc.databinding.ItemEventoBinding

class EventoAdapter(
    private val eventos: List<Evento>,
    private val onMenuItemClick: (Evento, Int) -> Unit
) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

    inner class EventoViewHolder(val binding: ItemEventoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val binding = ItemEventoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.binding.textViewNomeEvento.text = evento.nome
        holder.binding.textViewBairroEvento.text = evento.bairro

        // Menu de opções (três pontinhos)
        holder.binding.imageViewMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.event_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_event -> {
                        onMenuItemClick(evento, R.id.delete_event)
                        true
                    }
                    R.id.edit_event -> {
                        onMenuItemClick(evento, R.id.edit_event)
                        true
                    }
                    R.id.invite_event -> {
                        onMenuItemClick(evento, R.id.invite_event)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = eventos.size
}

package com.example.festapp_ofc

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.festapp_ofc.databinding.ItemUsuarioBinding

class UsuarioAdapter(
    private val usuarios: List<Usuario>,
    private val onUsuarioConvidado: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nomeTextView: TextView = binding.textViewUsuarioNome
        val convidarButton: Button = binding.buttonConvidar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.nomeTextView.text = usuario.nome

        holder.convidarButton.setOnClickListener {
            onUsuarioConvidado(usuario)
        }
    }

    override fun getItemCount(): Int = usuarios.size
}

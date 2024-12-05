package com.example.festapp_ofc

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festapp_ofc.databinding.ActivityBuscarEventoBinding
import com.example.festapp_ofc.databinding.ItemEventoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

data class Evento(
    var nome: String? = null,
    var bairro: String? = null,
    var id: String? = null // Campo local para armazenar o ID do documento
)

data class Usuario(
    val id: String,
    val nome: String
)

class BuscarEventoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuscarEventoBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: EventoAdapter
    private val eventoList = mutableListOf<Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarEventoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Configuração da RecyclerView
        adapter = EventoAdapter(eventoList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        carregarEventosDoConvidado()

        // Configuração da barra de pesquisa
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    buscarEventos(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    buscarEventos(newText)
                }
                return true
            }
        })

        // Ajuste de margens do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun buscarEventos(nome: String) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid // UID do usuário atual
        if (userUid != null) {
            db.collection("Eventos")
                .whereEqualTo("criadorUid", userUid) // Filtro para buscar apenas os eventos do criador
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        atualizarListaDeEventos(result)
                    } else {
                        Toast.makeText(this, "Nenhum evento encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao buscar eventos: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun atualizarListaDeEventos(result: QuerySnapshot) {
        eventoList.clear()
        for (document in result) {
            val evento = Evento(
                nome = document.getString("nome"),
                bairro = document.getString("bairro"),
                id = document.id
            )
            eventoList.add(evento)
        }

        adapter.notifyDataSetChanged()
    }

    private fun carregarEventosDoConvidado() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid != null) {
            val usuarioRef = Firebase.firestore.collection("Usuários").document(userUid)

            // Obtém os eventos do usuário
            usuarioRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val eventosIds = document.get("eventos") as? List<String> ?: emptyList()

                        // Se houver eventos, buscar os detalhes deles
                        if (eventosIds.isNotEmpty()) {
                            Firebase.firestore.collection("Eventos")
                                .whereIn(FieldPath.documentId(), eventosIds) // Filtra pelos IDs dos eventos
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val eventos = querySnapshot.documents.mapNotNull { doc ->
                                        doc.toObject(Evento::class.java)?.apply { id = doc.id }
                                    }

                                    // Atualiza a lista de eventos e notifica o adaptador
                                    eventoList.clear()
                                    eventoList.addAll(eventos)
                                    adapter.notifyDataSetChanged() // Atualiza a RecyclerView com os novos eventos
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, "Erro ao carregar eventos: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao carregar usuário: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    inner class EventoAdapter(private val eventos: List<Evento>) :
        RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

        inner class EventoViewHolder(val binding: ItemEventoBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
            val binding = ItemEventoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return EventoViewHolder(binding)
        }

        override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
            val evento = eventos[position]
            holder.binding.textViewNomeEvento.text = evento.nome
            holder.binding.textViewBairroEvento.text = evento.bairro

            holder.binding.imageViewMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.event_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.delete_event -> {
                            db.collection("Eventos").document(evento.id!!)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(view.context, "Evento '${evento.nome}' excluído!", Toast.LENGTH_SHORT).show()
                                    (eventos as MutableList).removeAt(position)
                                    notifyItemRemoved(position)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(view.context, "Erro ao excluir o evento.", Toast.LENGTH_SHORT).show()
                                }
                            true
                        }
                        R.id.edit_event -> {
                            val intent = Intent(view.context, EditarEventoActivity::class.java)
                            intent.putExtra("eventoId", evento.id)
                            view.context.startActivity(intent)
                            true
                        }
                        R.id.invite_event -> {
                            val intent = Intent(view.context, ConvidarUsuarioActivity::class.java)
                            intent.putExtra("eventoId", evento.id) // Passa o ID do evento para a nova tela
                            view.context.startActivity(intent)
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

}

package com.example.festapp_ofc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festapp_ofc.databinding.ActivityTelaPrincipalBinding
import com.example.festapp_ofc.databinding.ItemEventoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TelaPrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTelaPrincipalBinding
    private lateinit var eventoAdapter: EventoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Botão de sair
        binding.iconExit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val voltarTelaLogin = Intent(this, LoginActivity::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }

        // Navegação para outras telas
        binding.iconCalendar.setOnClickListener {
            startActivity(Intent(this, EventosPrincipalActivity::class.java))
        }
        binding.iconPerson.setOnClickListener {
            startActivity(Intent(this, UsuarioPrincipalActivity::class.java))
        }

        // Ajuste de padding com WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar RecyclerView
        binding.recyclerViewEventos.layoutManager = LinearLayoutManager(this)

        // Carregar eventos do Firestore
        carregarEventos()
    }

    private fun carregarEventos() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid // UID do usuário atual
        if (userUid != null) {
            Firebase.firestore.collection("Eventos")
                .whereEqualTo("criadorUid", userUid) // Filtra os eventos pelo UID do criador
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val eventos = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Evento::class.java)?.apply { id = doc.id }
                    }
                    eventoAdapter = EventoAdapter(eventos) { evento, itemId ->
                        when (itemId) {
                            R.id.delete_event -> excluirEvento(evento)
                            R.id.edit_event -> editarEvento(evento)
                            R.id.invite_event -> convidarParaEvento(evento)
                            R.id.payment_event -> pagamentoEvento(evento)
                        }
                    }
                    binding.recyclerViewEventos.adapter = eventoAdapter
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao carregar eventos: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun excluirEvento(evento: Evento) {
        val eventoId = evento.id
        if (eventoId != null) {
            Firebase.firestore.collection("Eventos").document(eventoId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Evento excluído com sucesso", Toast.LENGTH_SHORT).show()
                    carregarEventos() // Atualizar lista
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao excluir evento: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "ID do evento inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editarEvento(evento: Evento) {
        val intent = Intent(this, EditarEventoActivity::class.java).apply {
            putExtra("eventoId", evento.id)
            putExtra("eventoNome", evento.nome)
            putExtra("eventoBairro", evento.bairro)
            // Inclua outros dados conforme necessário
        }
        startActivity(intent)
    }

    private fun convidarParaEvento(evento: Evento) {
        Toast.makeText(this, "Funcionalidade de convite ainda não implementada", Toast.LENGTH_SHORT).show()
    }

    private fun pagamentoEvento(evento: Evento) {
        val intent = Intent(this, PixPaymentActivity::class.java)
        startActivity(intent)
    }

    inner class EventoAdapter(private val eventos: List<Evento>, private val onMenuItemClick: (Evento, Int) -> Unit) :
        RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

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
                        R.id.payment_event -> {
                            onMenuItemClick(evento, R.id.payment_event)
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
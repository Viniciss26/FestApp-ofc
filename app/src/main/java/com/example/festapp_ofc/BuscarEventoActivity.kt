package com.example.festapp_ofc

import Evento
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.festapp_ofc.databinding.ActivityBuscarEventoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

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
        db.collection("Eventos")
            .whereGreaterThanOrEqualTo("nome", nome)
            .whereLessThanOrEqualTo("nome", "$nome\uf8ff") // Pesquisa parcial
            .get()
            .addOnSuccessListener { result ->
                eventoList.clear() // Limpa a lista antes de adicionar os novos eventos
                var eventosEncontrados = false // Variável para verificar se algum evento foi encontrado

                for (document in result) {
                    val evento = Evento(
                        nome = document.getString("nome") ?: "",
                        bairro = document.getString("bairro") ?: ""
                    )
                    eventoList.add(evento)
                    eventosEncontrados = true
                }

                if (!eventosEncontrados) {
                    Toast.makeText(this, "Nenhum evento encontrado", Toast.LENGTH_SHORT).show()
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar eventos", Toast.LENGTH_SHORT).show()
            }
    }
}

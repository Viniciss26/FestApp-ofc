package com.example.festapp_ofc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityEditarEventoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.auth.FirebaseAuth

class EditarEventoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarEventoBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var eventoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // Obtém o eventoId a partir dos dados passados pela intent
        eventoId = intent.getStringExtra("eventoId")

        // Verifica se eventoId está disponível
        if (eventoId != null) {
            // Carregar dados do evento
            carregarEvento(eventoId!!)
        } else {
            Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show()
        }

        // Configura a aplicação de padding com relação à área da barra de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botão de salvar alterações
        binding.buttonCriarEvento.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun carregarEvento(eventoId: String) {
        // Busca o evento pelo ID no Firestore
        db.collection("Eventos").document(eventoId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    preencherCampos(documentSnapshot)
                } else {
                    Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar evento: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun preencherCampos(document: DocumentSnapshot) {
        // Preenche os campos com os dados do evento
        binding.editTextNomeEvento.setText(document.getString("nome"))
        binding.editTextDescricaoEvento.setText(document.getString("descricao"))
        binding.editTextDateEventoInicio.setText(document.getString("dataInicio"))
        binding.editTextTimeEventoInicio.setText(document.getString("horaInicio"))
        binding.editTextDateEventoTermino.setText(document.getString("dataTermino"))
        binding.editTextTimeEventoTermino.setText(document.getString("horaTermino"))
        binding.editTextLocalEvento.setText(document.getString("local"))
        binding.editTextLogradouroEvento.setText(document.getString("logradouro"))
        binding.editTextLocalEventoComplemento.setText(document.getString("complemento"))
        binding.editTextBairroEvento.setText(document.getString("bairro"))
        binding.editTextUfEvento.setText(document.getString("uf"))
        binding.editTextLocalidadeEvento.setText(document.getString("localidade"))
        binding.editTextCepEvento.setText(document.getString("cep"))
        binding.editTextNumberParticipantes.setText(document.getString("participantes"))
        binding.editTextNumberPreco.setText(document.getString("precoIngresso"))

        // Ajustes para preço e CEP
        val precoIngresso = document.get("precoIngresso")?.toString() ?: ""
        binding.editTextNumberPreco.setText(precoIngresso)

        val cep = document.get("cep")?.toString() ?: ""
        binding.editTextCepEvento.setText(cep)

        // Recupera o número de participantes
        val participantes = document.get("participantes")?.toString() ?: ""
        binding.editTextNumberParticipantes.setText(participantes)
    }

    private fun salvarAlteracoes() {
        val nome = binding.editTextNomeEvento.text.toString()
        val descricao = binding.editTextDescricaoEvento.text.toString()
        val dataInicio = binding.editTextDateEventoInicio.text.toString()
        val horaInicio = binding.editTextTimeEventoInicio.text.toString()
        val dataTermino = binding.editTextDateEventoTermino.text.toString()
        val horaTermino = binding.editTextTimeEventoTermino.text.toString()
        val local = binding.editTextLocalEvento.text.toString()
        val logradouro = binding.editTextLogradouroEvento.text.toString()
        val complemento = binding.editTextLocalEventoComplemento.text.toString()
        val bairro = binding.editTextBairroEvento.text.toString()
        val uf = binding.editTextUfEvento.text.toString()
        val localidade = binding.editTextLocalidadeEvento.text.toString()
        val cep = binding.editTextCepEvento.text.toString()
        val participantes = binding.editTextNumberParticipantes.text.toString()
        val preco = binding.editTextNumberPreco.text.toString()

        if (nome.isNotEmpty() && descricao.isNotEmpty()) {
            val evento = hashMapOf(
                "nome" to nome,
                "descricao" to descricao,
                "dataInicio" to dataInicio,
                "horaInicio" to horaInicio,
                "dataTermino" to dataTermino,
                "horaTermino" to horaTermino,
                "local" to local,
                "logradouro" to logradouro,
                "complemento" to complemento,
                "bairro" to bairro,
                "uf" to uf,
                "localidade" to localidade,
                "cep" to cep,
                "participantes" to participantes,
                "precoIngresso" to preco
            )

            // Atualiza o evento no Firestore
            db.collection("Eventos").document(eventoId!!)
                .set(evento)
                .addOnSuccessListener {
                    Toast.makeText(this, "Evento atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao atualizar evento: $e", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
        }
    }
}

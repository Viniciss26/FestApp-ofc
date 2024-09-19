package com.example.festapp_ofc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityBuscarEventoBinding
import com.google.firebase.firestore.FirebaseFirestore

class BuscarEventoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuscarEventoBinding
    private val db = FirebaseFirestore.getInstance()
    private var documentId: String? = null
    private var nomeEvento: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarEventoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.buttonBuscarEvento.setOnClickListener {
            val nomeEvento = binding.editTextNomeEventoBuscar.text.toString()
            if (nomeEvento.isNotEmpty()) {
                buscarEvento(nomeEvento)
            } else {
                Toast.makeText(this, "Por favor, insira o nome do evento.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonExcluirEvento.setOnClickListener {
            if (documentId != null) {
                excluirEvento(documentId!!)
            } else {
                Toast.makeText(this, "Pesquise um evento antes de excluí-lo.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonAtualizarEvento.setOnClickListener {
            if (documentId != null) {
                atualizarEvento(documentId!!)
            } else {
                Toast.makeText(this, "Pesquise um evento antes de atualizá-lo.", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun buscarEvento(nomeEvento: String) {
        db.collection("Eventos")
            .whereEqualTo("nome", nomeEvento)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (document in result) {
                        documentId = document.id
                        this.nomeEvento = document.getString("nome")
                        val descricaoEvento = document.getString("descricao")
                        val dataInicio = document.getString("dataInicio")
                        val horaInicio = document.getString("horaInicio")
                        val dataTermino = document.getString("dataTermino")
                        val horaTermino = document.getString("horaTermino")
                        val localEvento = document.getString("local")
                        val complementoEvento = document.getString("complemento")
                        val participantes = document.getString("participantes")
                        val preco = document.getString("preco")

                        binding.editTextDescricaoEventoBuscar.setText(descricaoEvento)
                        binding.editTextDateEventoInicioBuscar.setText(dataInicio)
                        binding.editTextTimeEventoInicioBuscar.setText(horaInicio)
                        binding.editTextDateEventoTerminoBuscar.setText(dataTermino)
                        binding.editTextTimeEventoTerminoBuscar.setText(horaTermino)
                        binding.editTextLocalEventoBuscar.setText(localEvento)
                        binding.editTextLocalEventoComplementoBuscar.setText(complementoEvento)
                        binding.editTextNumberParticipantesBuscar.setText(participantes)
                        binding.editTextNumberPrecoBuscar.setText(preco)

                        Toast.makeText(this, "Evento encontrado!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Evento não encontrado.", Toast.LENGTH_SHORT).show()
                    documentId = null
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar o evento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirEvento(documentId: String) {
        db.collection("Eventos").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Evento excluído com sucesso!", Toast.LENGTH_SHORT).show()
                limparCampos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao excluir o evento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atualizarEvento(documentId: String) {
        val eventoAtualizado = hashMapOf(
            "descricao" to binding.editTextDescricaoEventoBuscar.text.toString(),
            "dataInicio" to binding.editTextDateEventoInicioBuscar.text.toString(),
            "horaInicio" to binding.editTextTimeEventoInicioBuscar.text.toString(),
            "dataTermino" to binding.editTextDateEventoTerminoBuscar.text.toString(),
            "horaTermino" to binding.editTextTimeEventoTerminoBuscar.text.toString(),
            "local" to binding.editTextLocalEventoBuscar.text.toString(),
            "complemento" to binding.editTextLocalEventoComplementoBuscar.text.toString(),
            "participantes" to binding.editTextNumberParticipantesBuscar.text.toString(),
            "preco" to binding.editTextNumberPrecoBuscar.text.toString()
        )

        db.collection("Eventos").document(documentId)
            .set(eventoAtualizado)
            .addOnSuccessListener {
                Toast.makeText(this, "Evento atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao atualizar o evento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limparCampos() {
        binding.editTextDescricaoEventoBuscar.setText("")
        binding.editTextDateEventoInicioBuscar.setText("")
        binding.editTextTimeEventoInicioBuscar.setText("")
        binding.editTextDateEventoTerminoBuscar.setText("")
        binding.editTextTimeEventoTerminoBuscar.setText("")
        binding.editTextLocalEventoBuscar.setText("")
        binding.editTextLocalEventoComplementoBuscar.setText("")
        binding.editTextNumberParticipantesBuscar.setText("")
        binding.editTextNumberPrecoBuscar.setText("")
        documentId = null
    }
}

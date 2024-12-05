package com.example.festapp_ofc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.festapp_ofc.databinding.ActivityConvidarUsuarioBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class ConvidarUsuarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConvidarUsuarioBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConvidarUsuarioBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Receber o ID do evento
        val eventoId = intent.getStringExtra("eventoId")
        if (eventoId == null) {
            Toast.makeText(this, "Erro ao carregar evento", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configura a RecyclerView
        binding.recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)

        // Buscar usuários e configurar o adapter
        buscarUsuarios(eventoId)
    }

    private fun buscarUsuarios(eventoId: String) {
        db.collection("Usuários")
            .get()
            .addOnSuccessListener { result ->
                val usuarios = mutableListOf<Usuario>()
                for (document in result) {
                    val usuario = Usuario(
                        id = document.id,
                        nome = document.getString("nome") ?: "Sem nome"
                    )
                    usuarios.add(usuario)
                }

                // Configura o adapter com a lista de usuários
                val adapter = UsuarioAdapter(usuarios) { usuario ->
                    // Ao clicar em um item, envia o convite
                    enviarConvite(eventoId, usuario)
                }
                binding.recyclerViewUsuarios.adapter = adapter

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao buscar usuários: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enviarConvite(eventoId: String, usuario: Usuario) {
        // Formatar a data/hora do convite
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dataConvite = dateFormat.format(System.currentTimeMillis())

        // Criação do convite
        val convite = hashMapOf(
            "eventoId" to eventoId,
            "convidadoId" to usuario.id,
            "nomeConvidado" to usuario.nome,
            "dataConvite" to dataConvite
        )

        // Salvar o convite na coleção "Convites"
        db.collection("Convites")
            .add(convite)
            .addOnSuccessListener { documentReference ->
                // Atualizar a lista de eventos do convidado na coleção "Usuários"
                val convidadoRef = db.collection("Usuários").document(usuario.id)
                convidadoRef.update("eventos", FieldValue.arrayUnion(eventoId)) // Adiciona o evento à lista de eventos do usuário convidado

                Toast.makeText(this, "Convite enviado para: ${usuario.nome}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao enviar convite: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

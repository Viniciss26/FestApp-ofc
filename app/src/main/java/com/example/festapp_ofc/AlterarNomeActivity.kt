package com.example.festapp_ofc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityAlterarNomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class AlterarNomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlterarNomeBinding
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityAlterarNomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance() // Inicializa o Firestore

        // Configura o listener para o botão de atualização de nome
        binding.buttonAlterarName.setOnClickListener {
            val novoNome = binding.editTextNewName.text.toString().trim()
            if (novoNome.isNotEmpty()) {
                updateName(novoNome)
            } else {
                Toast.makeText(this, "Por favor, insira um novo nome.", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateName(newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Usando mutableMapOf para garantir a compatibilidade de tipos
            val userUpdates = mutableMapOf<String, Any>(
                "nome" to newName
            )
            firestore.collection("Usuários").document(userId)
                .update(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        binding.editTextNewName.text.clear() // Limpa o campo após a atualização
                    } else {
                        Toast.makeText(this, "Erro ao atualizar nome: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Usuário não está autenticado", Toast.LENGTH_SHORT).show()
        }
    }

}
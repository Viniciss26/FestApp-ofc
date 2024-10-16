package com.example.festapp_ofc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityAtualizarUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class AtualizarUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAtualizarUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o Firebase Auth e Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Ajusta o layout para os insets do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user = auth.currentUser
        if (user != null) {
            // Preencher o campo de email
            binding.editTextEmailUser.setText(user.email)

            // Buscar o nome do Firestore e preencher o campo de nome
            firestore.collection("Usuários").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nome = document.getString("nome")
                        binding.editTextNomeUser.setText(nome)
                    } else {
                        Toast.makeText(this, "Nome não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao buscar o nome.", Toast.LENGTH_SHORT).show()
                }
        }

        // Listener do botão de atualização
        binding.buttonAtualizarUser.setOnClickListener {
            val newNome = binding.editTextNomeUser.text.toString()
            val newEmail = binding.editTextEmailUser.text.toString()

            if (newNome.isNotEmpty()) {
                // Atualizar nome no Firestore
                firestore.collection("Usuários").document(user!!.uid)
                    .update("nome", newNome)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao atualizar nome.", Toast.LENGTH_SHORT).show()
                    }
            }

            if (user != null) {
                if (newEmail.isNotEmpty() && newEmail != user.email) {
                    // Atualizar email no Firebase Auth
                    user.updateEmail(newEmail)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Email atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Erro ao atualizar email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}

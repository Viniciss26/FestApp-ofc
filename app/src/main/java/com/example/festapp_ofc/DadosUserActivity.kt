package com.example.festapp_ofc

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityDadosUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DadosUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDadosUserBinding
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDadosUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance() // Inicializa o Firestore

        mostrarDadosUsuario()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun mostrarDadosUsuario() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("Usuários").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nome = document.getString("nome") ?: "Nome não encontrado"
                        val email = FirebaseAuth.getInstance().currentUser?.email ?: "Email não encontrado"

                        binding.textViewDadosNome.text = nome
                        binding.textViewDadosEmail.text = email
                    } else {
                        binding.textViewDadosNome.text = "Nome não encontrado"
                        binding.textViewDadosEmail.text = "Email não encontrado"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.textViewDadosNome.text = "Erro ao carregar nome: ${exception.message}"
                    binding.textViewDadosEmail.text = "Erro ao carregar email."
                }
        } else {
            binding.textViewDadosNome.text = "Usuário não autenticado"
            binding.textViewDadosEmail.text = "Usuário não autenticado"
        }
    }

}
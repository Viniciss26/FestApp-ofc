package com.example.festapp_ofc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityAtualizarUserBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AtualizarUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAtualizarUserBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonAlterarEmail.setOnClickListener {
            val novoEmail = binding.editTextNewEmail.text.toString().trim()
            if (novoEmail.isNotEmpty()) {
                updateEmail(novoEmail)
            } else {
                Toast.makeText(this, "Por favor, insira um novo email.", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateEmail(newEmail: String) {
        val user = auth.currentUser
        if (user != null) {
            user.verifyBeforeUpdateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email de verificação enviado para $newEmail", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao enviar email de verificação: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }

                    binding.editTextNewEmail.text.clear()
                }
        } else {
            Toast.makeText(this, "Usuário não está autenticado", Toast.LENGTH_SHORT).show()
        }
    }


}

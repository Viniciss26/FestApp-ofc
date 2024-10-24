package com.example.festapp_ofc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityAlterarSenhaBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class AlterarSenhaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlterarSenhaBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityAlterarSenhaBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonAlterarPassword.setOnClickListener {
            val newPassword = binding.editTextNewPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            } else {
                updatePassword(newPassword)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updatePassword(newPassword: String) {
        val user = auth.currentUser

        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
                        limparCampos()
                    } else {
                        Toast.makeText(this, "Erro ao alterar senha: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Usuário não está logado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limparCampos() {
        binding.editTextNewPassword.text.clear()
        binding.editTextConfirmPassword.text.clear()
    }

}
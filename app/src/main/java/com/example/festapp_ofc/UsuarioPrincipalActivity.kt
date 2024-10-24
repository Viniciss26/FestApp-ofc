package com.example.festapp_ofc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityTelaPrincipalBinding
import com.example.festapp_ofc.databinding.ActivityUsuarioPrincipalBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog  // Adicionado
import android.content.DialogInterface  // Adicionado
import android.widget.ImageButton
import android.widget.ImageView

class UsuarioPrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsuarioPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuarioPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.linearExcluir.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()

            if (user != null) {
                // Adiciona a caixa de diálogo de confirmação
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmar Exclusão da Conta")
                builder.setMessage("Você tem certeza de que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                builder.setPositiveButton("Sim") { dialog, _ ->
                    db.collection("Usuários").document(user.uid)
                        .delete()
                        .addOnSuccessListener {
                            user.delete().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Erro ao excluir a conta", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao excluir os dados do usuário", Toast.LENGTH_SHORT).show()
                        }
                    dialog.dismiss()
                }
                builder.setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                Toast.makeText(this, "Nenhum usuário autenticado", Toast.LENGTH_SHORT).show()
            }
        }

        binding.linearCadastro.setOnClickListener {
            val telaAtualizarUser = Intent(this, AtualizarUserActivity::class.java)
            startActivity(telaAtualizarUser)
        }

        binding.linearSenha.setOnClickListener {
            val telaAtualizarPassword = Intent(this, AlterarSenhaActivity::class.java)
            startActivity(telaAtualizarPassword)
        }

        binding.linearNome.setOnClickListener {
            val telaAtualizarNome = Intent(this, AlterarNomeActivity::class.java)
            startActivity(telaAtualizarNome)
        }

        binding.linearDados.setOnClickListener {
            val telaDadosUser = Intent(this, DadosUserActivity::class.java)
            startActivity(telaDadosUser)
        }

        binding.iconExit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val voltarTelaLogin = Intent(this, LoginActivity::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }
        val imageButton = findViewById<ImageButton>(R.id.iconCalendar)
        imageButton.setOnClickListener{
            val intent = Intent(this, EventosPrincipalActivity::class.java)
            startActivity(intent)
        }
        val imageButton2 = findViewById<ImageButton>(R.id.iconPerson)
        imageButton2.setOnClickListener{
            val intent = Intent(this, UsuarioPrincipalActivity::class.java)
            startActivity(intent)
        }
        val imageButton3 = findViewById<ImageButton>(R.id.iconHome)
        imageButton3.setOnClickListener {
            val intent = Intent(this,TelaPrincipalActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

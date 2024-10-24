package com.example.festapp_ofc

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityCadastroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.buttonCadastro.setOnClickListener { view ->
            val nome = binding.editTextNomeCadastro.text.toString()
            val email = binding.editTextEmailCadastro.text.toString()
            val senha = binding.editTextSenhaCadastro.text.toString()

            if (email.isEmpty() || senha.isEmpty() || nome.isEmpty()){
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }else{
                auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->
                    if(cadastro.isSuccessful){
                        val userId = auth.currentUser?.uid
                        if (userId != null){
                            armazenarDadosAdicionais(userId, nome)
                        }
                        val snackbar = Snackbar.make(view, "Sucesso ao cadastrar usuário!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                        binding.editTextNomeCadastro.setText("")
                        binding.editTextEmailCadastro.setText("")
                        binding.editTextSenhaCadastro.setText("")
                    }
                }.addOnFailureListener { exception ->
                    val mensagemErro = when(exception){
                        is FirebaseAuthWeakPasswordException -> "Senha de no mínimo 6 caracteres"
                        is FirebaseAuthInvalidCredentialsException -> "Digite um email Válido"
                        is FirebaseAuthUserCollisionException -> "Email ja está em uso!"
                        else -> "Erro ao concluir o cadastro"
                    }
                    val snackbar = Snackbar.make(view, mensagemErro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }

        val textView = findViewById<TextView>(R.id.entrar)
        textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun armazenarDadosAdicionais(userId: String, nome: String){
        val userData = hashMapOf(
            "nome" to nome
        )
        db.collection("Usuários").document(userId).set(userData)
    }

}
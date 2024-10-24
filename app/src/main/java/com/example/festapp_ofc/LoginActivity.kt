package com.example.festapp_ofc

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener { view ->
            val email = binding.editTextEmail.text.toString()
            val senha = binding.editTextTextPassword.text.toString()

            if (email.isEmpty() || senha.isEmpty()){
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }else{
                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { autenticacao ->
                    if (autenticacao.isSuccessful){
                        navTelaPrincipal()
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
        val textView = findViewById<TextView>(R.id.text_inscrever)
        textView.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun navTelaPrincipal(){
        val intent = Intent(this, TelaPrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if(usuarioAtual != null){
            navTelaPrincipal()
        }
    }
}

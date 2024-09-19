package com.example.festapp_ofc

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityTelaPrincipalBinding
import com.google.firebase.auth.FirebaseAuth

class TelaPrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTelaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val voltarTelaLogin = Intent(this, LoginActivity::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }
        val textView = findViewById<TextView>(R.id.text_eventos)
        textView.setOnClickListener{
            val intent = Intent(this, EventosPrincipalActivity::class.java)
            startActivity(intent)
        }
        val imageView = findViewById<ImageView>(R.id.imageView_usuario)
        imageView.setOnClickListener{
            val intent = Intent(this, UsuarioPrincipalActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
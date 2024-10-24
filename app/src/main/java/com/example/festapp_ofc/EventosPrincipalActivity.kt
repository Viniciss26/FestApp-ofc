package com.example.festapp_ofc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityEventosPrincipalBinding
import com.example.festapp_ofc.databinding.ActivityTelaPrincipalBinding
import com.google.firebase.auth.FirebaseAuth

class EventosPrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventosPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.linearCriarEvento.setOnClickListener {
            val telaCriarEvento = Intent(this, CriarEventoActivity::class.java)
            startActivity(telaCriarEvento)
        }

        binding.linearBuscarEvento.setOnClickListener {
            val telaBuscarEvento = Intent(this, BuscarEventoActivity::class.java)
            startActivity(telaBuscarEvento)
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
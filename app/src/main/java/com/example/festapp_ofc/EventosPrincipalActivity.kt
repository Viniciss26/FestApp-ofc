package com.example.festapp_ofc

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityEventosPrincipalBinding
import com.example.festapp_ofc.databinding.ActivityTelaPrincipalBinding

class EventosPrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventosPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnCriarEvento.setOnClickListener {
            val telaCriarEvento = Intent(this, CriarEventoActivity::class.java)
            startActivity(telaCriarEvento)
        }

        binding.btnBuscarEvento.setOnClickListener {
            val telaBuscarEvento = Intent(this, BuscarEventoActivity::class.java)
            startActivity(telaBuscarEvento)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
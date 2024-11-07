package com.example.festapp_ofc

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityCriarEventoBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CriarEventoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCriarEventoBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var startDateEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var endTimeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityCriarEventoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        startDateEditText = findViewById(R.id.editTextDate_evento_inicio)
        startTimeEditText = findViewById(R.id.editTextTime_evento_inicio)
        endDateEditText = findViewById(R.id.editTextDate_evento_termino)
        endTimeEditText = findViewById(R.id.editTextTime_evento_termino)

        startDateEditText.setOnClickListener {
            openDatePicker(startDateEditText)
        }

        startTimeEditText.setOnClickListener {
            openTimePicker(startTimeEditText)
        }

        endDateEditText.setOnClickListener {
            openDatePicker(endDateEditText)
        }

        endTimeEditText.setOnClickListener {
            openTimePicker(endTimeEditText)
        }

        // Iniciar o mapa e ativar e desativar a ScrollView
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        val scrollView = findViewById<ScrollView>(R.id.mainScrollView)

        mapFragment.getMapAsync { googleMap ->
            googleMap.setOnCameraMoveStartedListener {
                scrollView.requestDisallowInterceptTouchEvent(true)
            }

            googleMap.setOnCameraIdleListener {
                scrollView.requestDisallowInterceptTouchEvent(false)
            }
        }

        binding.editTextCepEvento.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val cep = s.toString()
                if (cep.length == 8) {
                    buscarEnderecoPorCep(cep)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.buttonCriarEvento.setOnClickListener {
            val nomeEvento = binding.editTextNomeEvento.text.toString()
            val descricaoEvento = binding.editTextDescricaoEvento.text.toString()
            val dataInicio = binding.editTextDateEventoInicio.text.toString()
            val horaInicio = binding.editTextTimeEventoInicio.text.toString()
            val dataTermino = binding.editTextDateEventoTermino.text.toString()
            val horaTermino = binding.editTextTimeEventoTermino.text.toString()
            val localEvento = binding.editTextLocalEvento.text.toString()
            val logradouroEvento = binding.editTextLogradouroEvento.text.toString()
            val complementoEvento = binding.editTextLocalEventoComplemento.text.toString()
            val bairroEvento = binding.editTextBairroEvento.text.toString()
            val ufEvento = binding.editTextUfEvento.text.toString()
            val localidadeEvento = binding.editTextLocalidadeEvento.text.toString()
            val cepEvento = binding.editTextCepEvento.text.toString()
            val participantesEvento = binding.editTextNumberParticipantes.text.toString()
            val precoIngresso = binding.editTextNumberPreco.text.toString()

            if (nomeEvento.isNotEmpty() && descricaoEvento.isNotEmpty() && dataInicio.isNotEmpty() && horaInicio.isNotEmpty()
                && dataTermino.isNotEmpty() && horaTermino.isNotEmpty() && localEvento.isNotEmpty() && logradouroEvento.isNotEmpty()
                && complementoEvento.isNotEmpty() && bairroEvento.isNotEmpty() && ufEvento.isNotEmpty() && localidadeEvento.isNotEmpty()
                && cepEvento.isNotEmpty() && participantesEvento.isNotEmpty() && precoIngresso.isNotEmpty()) {

                val eventoData = hashMapOf(
                    "nome" to nomeEvento,
                    "descricao" to descricaoEvento,
                    "dataInicio" to dataInicio,
                    "horaInicio" to horaInicio,
                    "dataTermino" to dataTermino,
                    "horaTermino" to horaTermino,
                    "local" to localEvento,
                    "logradouro" to logradouroEvento,
                    "complemento" to complementoEvento,
                    "bairro" to bairroEvento,
                    "uf" to ufEvento,
                    "localidade" to localidadeEvento,
                    "CEP" to cepEvento,
                    "participantes" to participantesEvento,
                    "preco" to precoIngresso
                )

                db.collection("Eventos")
                    .add(eventoData)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(this, "Evento criado com sucesso!", Toast.LENGTH_SHORT).show()

                        binding.editTextNomeEvento.text.clear()
                        binding.editTextDescricaoEvento.text.clear()
                        binding.editTextDateEventoInicio.text.clear()
                        binding.editTextTimeEventoInicio.text.clear()
                        binding.editTextDateEventoTermino.text.clear()
                        binding.editTextTimeEventoTermino.text.clear()
                        binding.editTextLocalEvento.text.clear()
                        binding.editTextLogradouroEvento.text.clear()
                        binding.editTextLocalEventoComplemento.text.clear()
                        binding.editTextBairroEvento.text.clear()
                        binding.editTextUfEvento.text.clear()
                        binding.editTextLocalidadeEvento.text.clear()
                        binding.editTextCepEvento.text.clear()
                        binding.editTextNumberParticipantes.text.clear()
                        binding.editTextNumberPreco.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao criar evento: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("CriarEventoActivity", "Erro ao criar evento", e)
                    }
            }else{
                Toast.makeText(this, "Por favor, Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun openDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            editText.setText(sdf.format(selectedDate.time))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun openTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            editText.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun buscarEnderecoPorCep(cep: String) {
        val url = "https://viacep.com.br/ws/$cep/json/"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        val jsonObject = JSONObject(json ?: "")
                        val local = jsonObject.optString("local")
                        val complemento = jsonObject.optString("complemento")
                        val logradouro = jsonObject.optString("logradouro")
                        val bairro = jsonObject.optString("bairro")
                        val uf = jsonObject.optString("uf")
                        val localidade = jsonObject.optString("localidade")

                        runOnUiThread {
                            // Preencher automaticamente os campos
                            binding.editTextLocalEvento.setText(local)
                            binding.editTextLocalEventoComplemento.setText(complemento)
                            binding.editTextLogradouroEvento.setText(logradouro)
                            binding.editTextBairroEvento.setText(bairro)
                            binding.editTextLocalidadeEvento.setText(localidade)
                            binding.editTextUfEvento.setText(uf)

                            // Atualizar o mapa
                            buscarCoordenadasPorEndereco("$local, $complemento, $logradouro, $bairro, $uf, $localidade")
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Erro na resposta da API.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao buscar endereço.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun atualizarMapa(latitude: Double, longitude: Double) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            // Limpa os marcadores existentes, se necessário
            googleMap.clear()

            // Cria um LatLng a partir da latitude e longitude
            val location = LatLng(latitude, longitude)

            // Move a câmera para a nova localização
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

            // Adiciona um marcador na nova localização
            googleMap.addMarker(MarkerOptions().position(location).title("Local do Evento"))
        }
    }



    private fun buscarCoordenadasPorEndereco(endereco: String) {
        val apiKey = "AIzaSyAWqni2JZ6gIG8SjDv2plzXZF0Or0JzrPw"
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=${Uri.encode(endereco)}&key=$apiKey"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        val jsonObject = JSONObject(json ?: "")
                        val results = jsonObject.getJSONArray("results")
                        if (results.length() > 0) {
                            val location = results.getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                            val latitude = location.getDouble("lat")
                            val longitude = location.getDouble("lng")

                            runOnUiThread {
                                atualizarMapa(latitude, longitude)
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Erro ao obter coordenadas.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao buscar coordenadas.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }





}
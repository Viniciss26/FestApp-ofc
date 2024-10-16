package com.example.festapp_ofc

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityCriarEventoBinding
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
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


        //val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment


        binding.buttonCriarEvento.setOnClickListener {
            val nomeEvento = binding.editTextNomeEvento.text.toString()
            val descricaoEvento = binding.editTextDescricaoEvento.text.toString()
            val dataInicio = binding.editTextDateEventoInicio.text.toString()
            val horaInicio = binding.editTextTimeEventoInicio.text.toString()
            val dataTermino = binding.editTextDateEventoTermino.text.toString()
            val horaTermino = binding.editTextTimeEventoTermino.text.toString()
            val localEvento = binding.editTextLocalEvento.text.toString()
            val complementoEvento = binding.editTextLocalEventoComplemento.text.toString()
            val participantesEvento = binding.editTextNumberParticipantes.text.toString()
            val precoIngresso = binding.editTextNumberPreco.text.toString()

            if (nomeEvento.isNotEmpty() && descricaoEvento.isNotEmpty() && dataInicio.isNotEmpty() && horaInicio.isNotEmpty()
                && dataTermino.isNotEmpty() && horaTermino.isNotEmpty() && localEvento.isNotEmpty() && complementoEvento.isNotEmpty()
                && participantesEvento.isNotEmpty() && precoIngresso.isNotEmpty()) {

                val eventoData = hashMapOf(
                    "nome" to nomeEvento,
                    "descricao" to descricaoEvento,
                    "dataInicio" to dataInicio,
                    "horaInicio" to horaInicio,
                    "dataTermino" to dataTermino,
                    "horaTermino" to horaTermino,
                    "local" to localEvento,
                    "complemento" to complementoEvento,
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
                        binding.editTextLocalEventoComplemento.text.clear()
                        binding.editTextNumberParticipantes.text.clear()
                        binding.editTextNumberPreco.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao criar evento", Toast.LENGTH_SHORT).show()
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

}
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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CriarEventoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCriarEventoBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityCriarEventoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

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


        //Data e Hora de Início
        val editTextDataInicio: EditText = findViewById(R.id.editTextDate_evento_inicio)
        val editTextHoraInicio: EditText = findViewById(R.id.editTextTime_evento_inicio)

        //Data e Hora de Término
        val editTextDataTermino: EditText = findViewById(R.id.editTextDate_evento_termino)
        val editTextHoraTermino: EditText = findViewById(R.id.editTextTime_evento_termino)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val dateSetListenerInicio = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectDate = Calendar.getInstance()
            selectDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectDate.time)
            editTextDataInicio.setText(formattedDate)
        }

        val dateSetListenerTermino = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectDate = Calendar.getInstance()
            selectDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectDate.time)
            editTextDataTermino.setText(formattedDate)
        }

        val timeSetListenerInicio = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val formattedTime = timeFormat.format(calendar.time)
            editTextHoraInicio.setText(formattedTime)
        }

        val timeSetListenerTermino = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val formattedTime = timeFormat.format(calendar.time)
            editTextHoraTermino.setText(formattedTime)
        }

        editTextDataInicio.setOnClickListener {
            DatePickerDialog(
                this, dateSetListenerInicio, year, month, day
            ).show()
        }

        editTextDataTermino.setOnClickListener {
            DatePickerDialog(
                this, dateSetListenerTermino, year, month, day
            ).show()
        }

        editTextHoraInicio.setOnClickListener {
            TimePickerDialog(
                this, timeSetListenerInicio, hour, minute, true
            ).show()
        }

        editTextHoraTermino.setOnClickListener {
            TimePickerDialog(
                this, timeSetListenerTermino, hour, minute, true
            ).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
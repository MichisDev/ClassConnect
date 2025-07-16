package com.example.agendadigital

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.example.agendadigital.databinding.ActivityCrearEventoPadreBinding
import com.google.firebase.auth.FirebaseAuth
import model.Evento
import repository.EventosRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Activity para que los padres creen eventos personales en el calendario escolar.
 *
 * Comportamiento:
 * - Permite al padre introducir un título, descripción, fecha y hora.
 * - Si no se selecciona fecha u hora, se usarán los valores actuales por defecto.
 * - Al pulsar el botón "Crear evento", se almacena en Firestore mediante el repositorio.
 *
 * Requiere que el usuario esté autenticado (FirebaseAuth) para asociar el evento a su UID.
 *
 * Campos importantes:
 * - `selectedFecha`: Fecha seleccionada por el usuario (formato yyyy-MM-dd).
 * - `selectedHora`: Hora seleccionada por el usuario (formato HH:mm).
 *
 * Métodos:
 * - `mostrarSelectorFecha()`: Lanza un DatePickerDialog para elegir la fecha.
 * - `mostrarSelectorHora()`: Lanza un TimePickerDialog para elegir la hora.
 * - `obtenerFechaActual()`, `obtenerHoraActual()`: Devuelven valores actuales por defecto.
 *
 * Dependencias:
 * - Firebase Authentication.
 * - `EventosRepository` para la lógica de persistencia.
 * - `Evento`: Modelo de datos para representar el evento.
 */
class CrearEventoPadre : AppCompatActivity() {
    private lateinit var binding: ActivityCrearEventoPadreBinding
    private val repo = EventosRepository()
    private lateinit var selectedFecha: String
    private lateinit var selectedHora: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearEventoPadreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCrearEventoPadre.setOnClickListener {
            val titulo = binding.etTituloPadre.text.toString()
            val descripcion = binding.etDescripcionPadre.text.toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            if (titulo.isNotEmpty()) {
                val nuevoEvento = Evento(
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = if (::selectedFecha.isInitialized) selectedFecha else obtenerFechaActual(),
                    hora = if (::selectedHora.isInitialized) selectedHora else obtenerHoraActual(),
                    tipo = "personal",
                    creadorUid = uid,
                    aulaId = "aula_1" // Se puede obtener dinámicamente más adelante
                )
                repo.crearEvento(
                    nuevoEvento,
                    onSuccess = {
                        Toast.makeText(this, "Evento creado", Toast.LENGTH_SHORT).show()
                        finish() // Vuelve a la pantalla anterior
                    },
                    onFailure = {
                        Toast.makeText(this, "Error al crear evento", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvFecha.setOnClickListener { mostrarSelectorFecha() }
        binding.tvHora.setOnClickListener { mostrarSelectorHora() }

    }

    private fun obtenerFechaActual(): String {
        val now = Calendar.getInstance()
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(now.time)
    }

    private fun obtenerHoraActual(): String {
        val now = Calendar.getInstance()
        val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formato.format(now.time)
    }

    private fun mostrarSelectorFecha() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedFecha = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.tvFecha.text = selectedFecha
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun mostrarSelectorHora() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedHora = String.format("%02d:%02d", hourOfDay, minute)
            binding.tvHora.text = selectedHora
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

}
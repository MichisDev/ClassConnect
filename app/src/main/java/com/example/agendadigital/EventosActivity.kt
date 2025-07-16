package com.example.agendadigital

import adapters.EventosAdapter
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivityEventosBinding
import com.google.firebase.auth.FirebaseAuth
import model.Evento
import viewmodel.EventosViewModel
import java.util.Calendar

/**
 * Actividad encargada de mostrar y gestionar los eventos creados por el usuario (profesor).
 * Permite crear, editar y eliminar eventos asociados a una clase.
 * Utiliza un ViewModel para obtener los datos desde Firebase.
 */
class EventosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosBinding
    private val viewModel: EventosViewModel by viewModels()
    private lateinit var adapter: EventosAdapter
    private lateinit var selectedFecha: String
    private lateinit var selectedHora: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Spinner con tipos de evento
        val tipos = listOf("Nota", "Tarea", "Excursión", "Examen", "Tutoría")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = spinnerAdapter

        // Spinner con privacidad
        val privacidad = listOf("Personal", "Común")
        val privacidadAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, privacidad)
        privacidadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrivacidad.adapter = privacidadAdapter

        // RecyclerView y adaptador con edición activa
        adapter = EventosAdapter(
            eventos = emptyList(),
            onEliminar = { confirmarEliminacion(it) },
            onEditar = { mostrarDialogoEdicion(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.eventos.observe(this) { eventos ->
            Log.d("EVENTOS", "Eventos cargados: ${eventos.size}")
            adapter.actualizarLista(eventos)
        }

        // Crear evento
        binding.btnCrearEvento.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid ?: ""

            val titulo = binding.etTitulo.text.toString()
            val descripcion = binding.etDescripcion.text.toString()
            val tipoEvento = binding.spinnerPrivacidad.selectedItem.toString().lowercase()
            val fecha = if (::selectedFecha.isInitialized) selectedFecha else obtenerFechaActual()
            val hora = if (::selectedHora.isInitialized) selectedHora else obtenerHoraActual()

            if (titulo.isNotEmpty()) {
                val nuevoEvento = Evento(
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fecha,
                    hora = hora,
                    tipo = tipoEvento,
                    creadorUid = uid,
                    aulaId = "aula_1" // de momento fija o la puedes obtener del usuario
                )
                viewModel.crearEvento(nuevoEvento)
                binding.etTitulo.text.clear()
                binding.etDescripcion.text.clear()
            }

        }
        binding.tvFecha.setOnClickListener { mostrarSelectorFecha() }
        binding.tvHora.setOnClickListener { mostrarSelectorHora() }

        // Observar eventos
        viewModel.eventos.observe(this, Observer { eventos ->
            adapter.actualizarLista(eventos)
        })

        // Observar errores
        viewModel.error.observe(this, Observer { hayError ->
            if (hayError) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Ha ocurrido un error al procesar los eventos.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        })

        // Cargar eventos
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return
        val esProfesor = true // o false según el rol del usuario real
        viewModel.setUsuario(uid, esProfesor)
        viewModel.cargarEventos()

    }

    /**
     * Mostrar diálogo para editar un evento
     */
    private fun mostrarDialogoEdicion(evento: Evento) {
        val view = layoutInflater.inflate(R.layout.dialog_editar_evento, null)
        val etTitulo = view.findViewById<EditText>(R.id.etTituloEditar)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcionEditar)
        val spinnerTipo = view.findViewById<Spinner>(R.id.spinnerTipoEditar)

        etTitulo.setText(evento.titulo)
        etDescripcion.setText(evento.descripcion)

        val tipos = listOf("Nota", "Tarea", "Excursión", "Examen", "Tutoría")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = spinnerAdapter
        spinnerTipo.setSelection(tipos.indexOf(evento.tipo))

        AlertDialog.Builder(this)
            .setTitle("Editar evento")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val actualizado = evento.copy(
                    titulo = etTitulo.text.toString(),
                    descripcion = etDescripcion.text.toString(),
                    tipo = spinnerTipo.selectedItem.toString()
                )
                viewModel.editarEvento(actualizado)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Confirmar antes de eliminar un evento
     */
    private fun confirmarEliminacion(evento: Evento) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar evento")
            .setMessage("¿Deseas eliminar \"${evento.titulo}\"?")
            .setPositiveButton("Sí") { _, _ ->
                viewModel.eliminarEvento(evento.id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    /**
     * Obtener la fecha actual
     */
    private fun obtenerFechaActual(): String {
        val now = java.util.Calendar.getInstance()
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formato.format(now.time)
    }

    /**
     * Obtener la hora actual
     */
    private fun obtenerHoraActual(): String {
        val now = java.util.Calendar.getInstance()
        val formato = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return formato.format(now.time)
    }

    /**
     * Mostrar selector de fecha
     */
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

    /**
     * Mostrar selector de hora
     */
    private fun mostrarSelectorHora() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedHora = String.format("%02d:%02d", hourOfDay, minute)
            binding.tvHora.text = selectedHora
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

}

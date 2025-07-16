package com.example.agendadigital

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivityEventosPadresBinding
import viewmodel.EventosViewModel
import adapters.EventosAdapter
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity que muestra al usuario con rol "padre" una lista de eventos escolares
 * y le permite crear eventos personales.
 *
 * Características principales:
 * - Muestra los eventos mediante un RecyclerView conectado a un ViewModel.
 * - Los eventos se muestran en modo solo lectura (no se pueden editar ni eliminar).
 * - Permite crear eventos personales (solo para el propio padre).
 *
 * Flujo general:
 * - Se inicializa la interfaz y se configura el adaptador del RecyclerView.
 * - Se observa el ViewModel para recibir la lista de eventos y posibles errores.
 * - Se filtran los eventos en función del usuario actual (padre).
 * - Se redirige a CrearEventoPadre al pulsar el botón correspondiente.
 *
 * Esta clase depende de:
 * - {@link EventosViewModel} para la lógica de negocio y acceso a datos.
 * - {@link EventosAdapter} para mostrar la lista de eventos.
 * - {@link CrearEventoPadre} para la creación de eventos personales.
 */
class EventosPadresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosPadresBinding
    private val viewModel: EventosViewModel by viewModels()
    private lateinit var adapter: EventosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosPadresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adaptador con solo lectura activado (sin botones)
        adapter = EventosAdapter(
            eventos = emptyList(),
            onEliminar = {
                Toast.makeText(this, "Los padres no pueden eliminar eventos", Toast.LENGTH_SHORT)
                    .show()
            },
            onEditar = {
                Toast.makeText(this, "Los padres no pueden editar eventos", Toast.LENGTH_SHORT)
                    .show()
            },
            soloLectura = true
        )

        // Configurar RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Observar cambios en la lista de eventos
        viewModel.eventos.observe(this, Observer { eventos ->
            adapter.actualizarLista(eventos)
        })

        viewModel.error.observe(this, Observer { error ->
            if (error) {
                Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
            }
        })

        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return
        val esProfesor = false // ¡Muy importante para el filtro!

        viewModel.setUsuario(uid, esProfesor)
        viewModel.cargarEventos()


        // Botón para crear evento personal
        binding.btnNuevoEventoPadre.setOnClickListener {
            val intent = Intent(this, CrearEventoPadre::class.java)
            startActivity(intent)
        }
    }
}

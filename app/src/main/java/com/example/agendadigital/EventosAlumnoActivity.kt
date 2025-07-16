package com.example.agendadigital

import adapters.EventosAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.Evento

/**
 * Activity que muestra los eventos relevantes para un alumno.
 *
 * Esta pantalla permite al alumno consultar los eventos personales creados por él
 * y los eventos comunes asignados a su aula.
 *
 * Características:
 * - Obtiene el `aulaId` del alumno actual desde Firestore.
 * - Filtra los eventos según el tipo ("personal" o "comun").
 * - Muestra los eventos en un RecyclerView de solo lectura.
 *
 * Restricciones:
 * - No permite editar ni eliminar eventos (modo soloLectura = true).
 *
 * Dependencias:
 * - Firestore (colecciones: "usuarios", "eventos").
 * - FirebaseAuth para identificar al alumno autenticado.
 * - {@link adapters.EventosAdapter} para renderizar la lista.
 *
 * Layout asociado:
 * - R.layout.activity_eventos_alumno (requiere RecyclerView con id rvEventosAlumno)
 */
class EventosAlumnoActivity : AppCompatActivity() {

    private lateinit var rvEventos: RecyclerView
    private lateinit var adapterEventos: EventosAdapter

    private val eventos = mutableListOf<Evento>()
    private val db = FirebaseFirestore.getInstance()
    private val alumnoId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_alumno)

        rvEventos = findViewById(R.id.rvEventosAlumno)
        rvEventos.layoutManager = LinearLayoutManager(this)

        adapterEventos = EventosAdapter(
            eventos,
            onEliminar = {},
            onEditar = {},
            soloLectura = true
        )
        rvEventos.adapter = adapterEventos

        cargarEventos()
    }

    private fun cargarEventos() {
        db.collection("usuarios").document(alumnoId).get()
            .addOnSuccessListener { alumnoDoc ->
                val aulaId = alumnoDoc.getString("aulaId") ?: ""
                db.collection("eventos")
                    .whereIn("tipo", listOf("personal", "comun"))
                    .get()
                    .addOnSuccessListener { result ->
                        val eventosFiltrados = result.mapNotNull { doc ->
                            val evento = doc.toObject(Evento::class.java)
                            if (
                                (evento.tipo == "personal" && evento.creadorUid == alumnoId) ||
                                (evento.tipo == "comun" && evento.aulaId == aulaId)
                            ) evento else null
                        }
                        adapterEventos.actualizarLista(eventosFiltrados)
                    }
            }
    }
}

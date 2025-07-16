package com.example.agendadigital

import adapters.EventoDialogAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.EventDay
import com.example.agendadigital.databinding.ActivityCalendarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.Evento
import java.text.SimpleDateFormat
import java.util.*
import com.applandeo.materialcalendarview.listeners.OnDayClickListener


/**
 * Muestra un calendario con eventos personales y comunes.
 * - Los profesores ven todos los eventos comunes y los personales que han creado.
 * - Los padres solo ven eventos comunes y sus eventos personales.
 * Al hacer clic sobre un día, se muestran los eventos de esa fecha.
 */
class CalendarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarioBinding
    private val db = FirebaseFirestore.getInstance()
    private val eventosRef = db.collection("eventos")
    private lateinit var tipoUsuario: String
    private lateinit var uid: String
    private val mapaEventos = mutableMapOf<String, MutableList<Evento>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tipoUsuario = intent.getStringExtra("tipoUsuario") ?: "desconocido"
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        title = if (tipoUsuario == "profesor") "Calendario del profesor" else "Calendario del padre"

        cargarEventosCalendario()

        // Configura el listener para mostrar eventos al hacer clic en un día
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val fecha = eventDay.calendar
                val fechaFormateada =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha.time)
                val clave = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha.time)

                val eventosDelDia = mapaEventos[clave]

                if (!eventosDelDia.isNullOrEmpty()) {
                    val mensaje = eventosDelDia.joinToString("\n") {
                        "\uD83D\uDCC5 ${it.titulo} (${it.tipo} - ${it.hora})"
                    }

                    val dialogView = layoutInflater.inflate(R.layout.dialog_lista_eventos, null)
                    val recyclerView = dialogView.findViewById<RecyclerView>(R.id.listaEventos)
                    recyclerView.layoutManager = LinearLayoutManager(this@CalendarioActivity)
                    recyclerView.adapter = EventoDialogAdapter(eventosDelDia)

                    AlertDialog.Builder(this@CalendarioActivity)
                        .setTitle("Eventos del $fechaFormateada")
                        .setView(dialogView)
                        .setPositiveButton("Cerrar", null)
                        .show()


                } else {
                    Toast.makeText(
                        this@CalendarioActivity,
                        "Sin eventos ese día",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    /**
     * Carga todos los eventos desde Firestore y los agrupa por fecha.
     * Luego transforma las fechas a `EventDay` para mostrar en el calendario con distintos iconos.
     */
    private fun cargarEventosCalendario() {
        eventosRef.get().addOnSuccessListener { result ->
            val eventosVisuales = mutableListOf<EventDay>()
            mapaEventos.clear()

            for (doc in result) {
                val evento = doc.toObject(Evento::class.java)

                val esEventoVisible = when {
                    evento.tipo.lowercase(Locale.getDefault()) == "comun" -> true
                    evento.tipo.lowercase(Locale.getDefault()) == "personal" && evento.creadorUid == uid -> true
                    else -> false
                }

                if (esEventoVisible) {
                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val clave = evento.fecha
                    val calendar = Calendar.getInstance()

                    try {
                        val fechaParseada = formato.parse(evento.fecha)
                        calendar.time = fechaParseada ?: continue
                    } catch (e: Exception) {
                        e.printStackTrace()
                        continue
                    }

                    // Guardar eventos por fecha
                    val lista = mapaEventos.getOrPut(clave) { mutableListOf() }
                    lista.add(evento)
                }
            }

            // Convertir a eventos visuales con iconos
            for ((fecha, listaEventos) in mapaEventos) {
                val calendar = Calendar.getInstance()
                try {
                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val fechaParseada = formato.parse(fecha)
                    calendar.time = fechaParseada ?: continue
                } catch (e: Exception) {
                    continue
                }

                val tipos = listaEventos.map { it.tipo.lowercase(Locale.getDefault()) }.toSet()
                val icono = when {
                    tipos.contains("comun") && tipos.contains("personal") -> R.drawable.ic_arco_iris
                    tipos.contains("comun") -> R.drawable.ic_evento_naranja
                    else -> R.drawable.ic_evento_azul
                }


                eventosVisuales.add(EventDay(calendar, icono))
            }

            binding.calendarView.setEvents(eventosVisuales)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
        }
    }
}


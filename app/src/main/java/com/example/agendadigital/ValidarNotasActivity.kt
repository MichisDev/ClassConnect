package com.example.agendadigital

import adapters.NotasPendientesAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import model.Nota

/**
 * Activity para que los profesores validen las notas introducidas por alumnos.
 *
 * - Carga automáticamente todas las notas que aún no han sido validadas.
 * - Muestra cada nota pendiente en un RecyclerView.
 * - Permite al profesor validar una nota pulsando sobre ella y confirmando en un diálogo.
 */
class ValidarNotasActivity : AppCompatActivity() {

    private lateinit var rvNotasPendientes: RecyclerView
    private lateinit var adapter: NotasPendientesAdapter
    private val db = FirebaseFirestore.getInstance()
    private val notasPendientes = mutableListOf<Triple<String, String, Nota>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validar_notas)

        rvNotasPendientes = findViewById(R.id.rvNotasPendientes)
        rvNotasPendientes.layoutManager = LinearLayoutManager(this)

        adapter = NotasPendientesAdapter(notasPendientes) { alumnoId, nota ->
            mostrarDialogoValidacion(alumnoId, nota)
        }

        rvNotasPendientes.adapter = adapter

        cargarNotasNoValidadas()
    }

    /**
     * Carga todas las notas no validadas desde Firestore y las muestra en el RecyclerView.
     */
    private fun cargarNotasNoValidadas() {
        notasPendientes.clear()
        adapter.notifyDataSetChanged()

        db.collection("usuarios")
            .whereEqualTo("rol", "alumno")
            .get()
            .addOnSuccessListener { alumnos ->
                if (alumnos.isEmpty) return@addOnSuccessListener

                var totalPendientes = 0
                var procesados = 0

                for (alumnoDoc in alumnos) {
                    val alumnoId = alumnoDoc.id
                    val nombre = alumnoDoc.getString("nombre") ?: "Desconocido"

                    db.collection("usuarios").document(alumnoId).collection("notas")
                        .whereEqualTo("validada", false)
                        .get()
                        .addOnSuccessListener { notas ->
                            for (notaDoc in notas) {
                                val nota = notaDoc.toObject(Nota::class.java).copy(id = notaDoc.id)
                                notasPendientes.add(Triple(alumnoId, nombre, nota))
                                totalPendientes++
                            }

                            procesados++
                            if (procesados == alumnos.size()) {
                                adapter.notifyDataSetChanged()
                                Toast.makeText(
                                    this,
                                    "Notas pendientes: $totalPendientes",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar alumnos", Toast.LENGTH_SHORT).show()
            }
    }



    /**
     * Muestra un diálogo de confirmación para validar una nota específica.
     *
     * @param alumnoId ID del alumno propietario de la nota.
     * @param nota Objeto Nota a validar.
     */
    private fun mostrarDialogoValidacion(alumnoId: String, nota: Nota) {
        AlertDialog.Builder(this)
            .setTitle("Validar nota")
            .setMessage("¿Validar \"${nota.titulo}\" de ${nota.asignatura}?\nNota: ${nota.calificacion}\nFecha: ${nota.fecha}")
            .setPositiveButton("Validar") { _, _ ->
                db.collection("usuarios").document(alumnoId)
                    .collection("notas").document(nota.id)
                    .update("validada", true)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nota validada", Toast.LENGTH_SHORT).show()
                        cargarNotasNoValidadas()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

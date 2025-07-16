package com.example.agendadigital

import adapters.NotasAdapter
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivityNotasAlumnoBinding
import com.example.agendadigital.databinding.DialogNuevaNotaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import model.Nota
import java.util.Calendar

/**
 * Activity que permite a los alumnos consultar sus calificaciones.
 *
 * Funcionalidades:
 * - Visualiza las notas del alumno en un {@link RecyclerView}.
 * - Permite añadir nuevas notas en estado de "borrador" (no validadas).
 *   Estas notas se introducen mediante un diálogo personalizado.
 *
 * Datos:
 * - Las notas están almacenadas en la subcolección "notas" dentro del documento del usuario con rol alumno.
 * - Cada nota contiene información como asignatura, calificación, fecha y si está validada.
 * - Las notas se muestran en orden descendente según el timestamp.
 *
 * Componentes utilizados:
 * - {@link RecyclerView} para mostrar la lista de notas.
 * - {@link NotasAdapter} para enlazar los datos al RecyclerView.
 * - {@link FirebaseFirestore} para cargar y guardar datos en Firestore.
 * - {@link FirebaseAuth} para obtener el ID del usuario autenticado.
 * - {@link AlertDialog} para crear nuevas notas.
 *
 * Requisitos:
 * - El usuario debe estar autenticado con FirebaseAuth.
 *
 * Nota:
 * - Las notas creadas desde esta Activity no están validadas por defecto.
 *   Se asume que un profesor las validará posteriormente en otra interfaz.
 */
class NotasAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotasAlumnoBinding
    private lateinit var adapterNotas: NotasAdapter

    private val notas = mutableListOf<Nota>()
    private val db = FirebaseFirestore.getInstance()
    private val alumnoId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotasAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNotasAlumno.layoutManager = LinearLayoutManager(this)
        adapterNotas = NotasAdapter(notas)
        binding.rvNotasAlumno.adapter = adapterNotas

        binding.btnNuevaNota.setOnClickListener { mostrarDialogoNuevaNota() }

        cargarNotas()
    }

    /**
     * Carga las notas del alumno desde Firestore y las muestra en el RecyclerView.
     */
    private fun cargarNotas() {
        db.collection("usuarios").document(alumnoId).collection("notas")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                notas.clear()
                for (doc in snapshots!!) {
                    notas.add(doc.toObject(Nota::class.java))
                }
                adapterNotas.notifyDataSetChanged()
            }
    }

    /**
     * Muestra un diálogo personalizado para crear una nueva nota.
     */
    private fun mostrarDialogoNuevaNota() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val etTitulo = EditText(this).apply {
            hint = "Título del examen (Ej: Examen unidad 4)"
        }

        val spinner = Spinner(this)
        val asignaturas = listOf("Matemáticas", "Lengua", "Ciencias", "Historia", "Inglés")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, asignaturas)
        spinner.adapter = adapterSpinner

        val etCalificacion = EditText(this).apply {
            hint = "Calificación (0 - 10)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val btnFecha = Button(this).apply {
            text = "Seleccionar fecha"
        }

        var fechaSeleccionada = ""

        btnFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, day)
                    btnFecha.text = "📅 $fechaSeleccionada"
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        layout.apply {
            addView(etTitulo)
            addView(spinner)
            addView(etCalificacion)
            addView(btnFecha)
        }

        val dialog = AlertDialog.Builder(this, R.style.AgendaDialogTheme)
            .setTitle("Nueva Nota (borrador)")
            .setView(layout)
            .setPositiveButton("Guardar", null) // lo seteamos manualmente luego
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val botonGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            botonGuardar.setTextColor(getColor(R.color.md_theme_primary))
            botonGuardar.setOnClickListener {
                val titulo = etTitulo.text.toString().trim()
                val asignatura = spinner.selectedItem.toString()
                val calificacionTexto = etCalificacion.text.toString().trim()
                val calificacion = calificacionTexto.toDoubleOrNull()

                if (titulo.isEmpty()) {
                    Toast.makeText(this, "Debes indicar el título del examen", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (calificacion == null || calificacion < 0 || calificacion > 10) {
                    Toast.makeText(this, "Nota no válida. Introduce un valor entre 0 y 10.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (fechaSeleccionada.isEmpty()) {
                    Toast.makeText(this, "Selecciona una fecha del examen", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val id = db.collection("usuarios").document(alumnoId)
                    .collection("notas").document().id

                val nuevaNota = Nota(
                    id = id,
                    asignatura = asignatura,
                    calificacion = calificacion,
                    titulo = titulo,
                    fecha = fechaSeleccionada,
                    timestamp = System.currentTimeMillis(),
                    validada = false
                )

                db.collection("usuarios").document(alumnoId)
                    .collection("notas").document(id)
                    .set(nuevaNota)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nota añadida correctamente", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
            }

            val botonCancelar = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            botonCancelar.setTextColor(getColor(R.color.md_theme_primary))
        }

        dialog.show()
    }


}

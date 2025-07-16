package com.example.agendadigital

import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.view.Gravity
import adapters.ApunteAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import model.Apunte

/**
 * Activity que permite a los alumnos gestionar sus apuntes personales.
 *
 * Funcionalidades principales:
 * - Visualizar una lista de apuntes almacenados en Firestore.
 * - Crear un nuevo apunte mediante un diálogo de entrada.
 * - Editar o eliminar apuntes existentes con opciones contextuales.
 *
 * Datos:
 * - Los apuntes se almacenan en la subcolección "apuntes" dentro del documento del usuario con rol "alumno".
 * - Se identifican por un ID generado automáticamente y se ordenan por marca de tiempo descendente.
 *
 * Componentes utilizados:
 * - {@link RecyclerView} para mostrar los apuntes.
 * - {@link ApunteAdapter} para enlazar los datos al RecyclerView.
 * - {@link FirebaseFirestore} para operaciones CRUD.
 * - {@link AlertDialog} para diálogos de entrada, edición y confirmación.
 *
 * Requisitos:
 * - El usuario debe estar autenticado con FirebaseAuth.
 *
 * Esta pantalla es de uso exclusivo para alumnos y sus datos personales.
 */
class ApuntesAlumnoActivity : AppCompatActivity() {

    private lateinit var rvApuntes: RecyclerView
    private lateinit var btnNuevoApunte: Button
    private lateinit var adapterApuntes: ApunteAdapter

    private val db = FirebaseFirestore.getInstance()
    private val alumnoId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val apuntes = mutableListOf<Apunte>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apuntes_alumno)

        rvApuntes = findViewById(R.id.rvApuntesAlumno)
        btnNuevoApunte = findViewById(R.id.btnNuevoApunteAlumno)

        // Configuración del RecyclerView
        adapterApuntes = ApunteAdapter(
            apuntes,
            onClick = { apunte -> mostrarDialogoEditarApunte(apunte) },
            onLongClick = { apunte -> mostrarDialogoOpciones(apunte) }
        )

        rvApuntes.layoutManager = LinearLayoutManager(this)
        rvApuntes.adapter = adapterApuntes

        btnNuevoApunte.setOnClickListener { mostrarDialogoNuevoApunte() }

        cargarApuntes()
    }

    /**
     * Carga los apuntes del alumno desde Firestore y los muestra en el RecyclerView.
     */
    private fun cargarApuntes() {
        db.collection("usuarios").document(alumnoId).collection("apuntes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                apuntes.clear()
                for (doc in snapshots!!) {
                    apuntes.add(doc.toObject(Apunte::class.java))
                }
                adapterApuntes.notifyDataSetChanged()
            }
    }

    /**
     * Muestra un diálogo para crear un nuevo apunte.
     */
    private fun mostrarDialogoNuevoApunte() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val spinner = Spinner(this)
        val asignaturas = listOf("Matemáticas", "Lengua", "Ciencias", "Historia", "Inglés")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, asignaturas)
        spinner.adapter = adapter

        val editTextContenido = EditText(this).apply {
            hint = "Escribe tu apunte aquí"
            minLines = 5
            setLines(8)
            gravity = Gravity.TOP
            inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
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

        layout.addView(spinner)
        layout.addView(btnFecha)
        layout.addView(editTextContenido)

        val dialog = AlertDialog.Builder(this, R.style.AgendaDialogTheme)
            .setTitle("Nuevo Apunte")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val contenido = editTextContenido.text.toString().trim()
                val asignatura = spinner.selectedItem.toString()
                val fecha = if (fechaSeleccionada.isNotEmpty()) fechaSeleccionada else {
                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    formato.format(Calendar.getInstance().time)
                }

                if (contenido.isNotEmpty()) {
                    val id = db.collection("apuntes").document().id
                    val nuevoApunte = Apunte(
                        id = id,
                        contenido = contenido,
                        asignatura = asignatura,
                        fecha = fecha,
                        timestamp = System.currentTimeMillis()
                    )

                    db.collection("usuarios").document(alumnoId)
                        .collection("apuntes").document(id).set(nuevoApunte)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positive?.apply {
                setTextColor(getColor(R.color.md_theme_primary))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }

            negative?.apply {
                setTextColor(getColor(R.color.md_theme_primary))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
        }


        dialog.show()
    }


    /**
     *  Muestra un diálogo contextual con opciones para editar o eliminar un apunte.
     */
    private fun mostrarDialogoOpciones(apunte: Apunte) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this, R.style.AgendaDialogTheme)
            .setTitle("Acción sobre el apunte")
            .setItems(opciones) { _, i ->
                when (i) {
                    0 -> mostrarDialogoEditarApunte(apunte)
                    1 -> eliminarApunte(apunte)
                }
            }
            .show()
    }

    /**
     * Muestra un diálogo para editar el contenido de un apunte.
     */
    private fun mostrarDialogoEditarApunte(apunte: Apunte) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val spinner = Spinner(this)
        val asignaturas = listOf("Matemáticas", "Lengua", "Ciencias", "Historia", "Inglés")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, asignaturas)
        spinner.adapter = adapter
        val indexAsignatura = asignaturas.indexOf(apunte.asignatura)
        if (indexAsignatura >= 0) spinner.setSelection(indexAsignatura)

        val editTextContenido = EditText(this).apply {
            setText(apunte.contenido)
            minLines = 5
            setLines(8)
            gravity = Gravity.TOP
            inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }

        val btnFecha = Button(this).apply {
            text = "📅 ${apunte.fecha.ifEmpty { "Seleccionar fecha" }}"
        }

        var fechaSeleccionada = apunte.fecha

        btnFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            if (fechaSeleccionada.isNotEmpty()) {
                val parts = fechaSeleccionada.split("-").map { it.toInt() }
                calendar.set(parts[0], parts[1] - 1, parts[2])
            }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, day)
                    btnFecha.text = "📅 $fechaSeleccionada"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        layout.addView(spinner)
        layout.addView(btnFecha)
        layout.addView(editTextContenido)

        AlertDialog.Builder(this, R.style.AgendaDialogTheme)
            .setTitle("Editar Apunte")
            .setView(layout)
            .setPositiveButton("Actualizar") { _, _ ->
                val nuevoTexto = editTextContenido.text.toString().trim()
                val nuevaAsignatura = spinner.selectedItem.toString()
                val nuevaFecha = fechaSeleccionada

                if (nuevoTexto.isNotEmpty()) {
                    val docRef = db.collection("usuarios").document(alumnoId)
                        .collection("apuntes").document(apunte.id)

                    docRef.update(
                        mapOf(
                            "contenido" to nuevoTexto,
                            "asignatura" to nuevaAsignatura,
                            "fecha" to nuevaFecha
                        )
                    )
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    /**
     * Muestra un diálogo de confirmación para eliminar un apunte.
     */
    private fun eliminarApunte(apunte: Apunte) {
        AlertDialog.Builder(this, R.style.AgendaDialogTheme)
            .setTitle("Eliminar apunte")
            .setMessage("¿Estás seguro de que deseas eliminar este apunte?")
            .setPositiveButton("Sí") { _, _ ->
                db.collection("usuarios").document(alumnoId)
                    .collection("apuntes").document(apunte.id)
                    .delete()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

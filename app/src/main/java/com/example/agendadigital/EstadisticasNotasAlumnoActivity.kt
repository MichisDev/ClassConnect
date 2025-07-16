package com.example.agendadigital

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.Nota

/**
 * Activity que muestra un gráfico de barras con las calificaciones validadas del alumno.
 *
 * Funcionalidades:
 * - Obtiene las notas validadas del alumno desde Firestore.
 * - Genera un gráfico de barras agrupado por asignatura con la librería MPAndroidChart.
 *
 * Datos:
 * - Las notas se obtienen de la subcolección "notas" del documento del usuario con rol alumno.
 * - Solo se consideran las notas con el campo "validada" en true.
 *
 * Componentes utilizados:
 * - {@link BarChart} de MPAndroidChart para visualizar los datos.
 * - {@link FirebaseFirestore} para obtener las notas del alumno.
 * - {@link FirebaseAuth} para identificar al alumno autenticado.
 *
 * Requisitos:
 * - El usuario debe estar autenticado en Firebase.
 *
 * Estilo:
 * - Usa colores de la paleta Material para un diseño visual coherente con la app.
 * - La interfaz se adapta al tema definido en el estilo AgendaDigital.
 */
class EstadisticasNotasAlumnoActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private val db = FirebaseFirestore.getInstance()
    private val alumnoId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas_notas_alumno)

        barChart = findViewById(R.id.barChartNotas)
        cargarNotasYMostrarGrafico()
    }

    private fun cargarNotasYMostrarGrafico() {
        db.collection("usuarios").document(alumnoId).collection("notas")
            .whereEqualTo("validada", true)
            .get()
            .addOnSuccessListener { notasSnapshot ->
                val notas = notasSnapshot.map { it.toObject(Nota::class.java) }

                val entradas = mutableListOf<BarEntry>()
                val asignaturas = mutableListOf<String>()

                notas.forEachIndexed { index, nota ->
                    entradas.add(BarEntry(index.toFloat(), nota.calificacion.toFloat()))
                    asignaturas.add(nota.asignatura)
                }

                val dataSet = BarDataSet(entradas, "Notas por asignatura")
                dataSet.color = Color.parseColor("#2196F3")

                val barData = BarData(dataSet)
                barData.barWidth = 0.9f

                barChart.apply {
                    data = barData
                    setFitBars(true)
                    description.isEnabled = false
                    xAxis.valueFormatter = IndexAxisValueFormatter(asignaturas)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    xAxis.labelRotationAngle = -45f
                    axisRight.isEnabled = false
                    animateY(1000)
                    invalidate()
                }
            }
    }
}

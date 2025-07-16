package adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadigital.R
import model.Nota
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptador para mostrar una lista de notas pendientes de validación por parte del profesor.
 *
 * Cada ítem representa una nota propuesta para un alumno, incluyendo:
 * - Asignatura.
 * - Calificación.
 * - Fecha de propuesta.
 * - Estado de validación (pendiente o validada).
 *
 * @property lista Lista de pares (alumnoId, Nota), donde alumnoId identifica al alumno.
 * @property onValidarClick Función lambda que se invoca al pulsar sobre un ítem para validar la nota.
 */
class NotasPendientesAdapter(
    private val lista: List<Triple<String, String, Nota>>,
    private val onItemClick: (String, Nota) -> Unit
) : RecyclerView.Adapter<NotasPendientesAdapter.NotaViewHolder>() {

    class NotaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreAlumno: TextView = view.findViewById(R.id.tvNombreAlumno)
        val tvAsignatura: TextView = view.findViewById(R.id.tvAsignaturaPendiente)
        val tvNota: TextView = view.findViewById(R.id.tvNotaPendiente)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaNotaPendiente)
        val tvEstado: TextView = view.findViewById(R.id.tvEstadoNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota_pendiente, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val (alumnoId, nombre, nota) = lista[position]

        holder.tvNombreAlumno.text = "Alumno: $nombre"
        holder.tvAsignatura.text = "Asignatura: ${nota.asignatura}"
        holder.tvNota.text = "Nota propuesta: ${nota.calificacion}"
        holder.tvFecha.text = "Fecha: ${nota.fecha}"
        holder.tvEstado.text = if (nota.validada) "✅ Validada" else "⏳ Pendiente de validación"
        holder.tvEstado.setTextColor(
            if (nota.validada)
                Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
        )

        holder.itemView.setOnClickListener { onItemClick(alumnoId, nota) }
    }

    override fun getItemCount(): Int = lista.size
}

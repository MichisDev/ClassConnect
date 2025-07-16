package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadigital.R
import model.Apunte

/**
 * Adaptador para mostrar una lista de apuntes en un RecyclerView.
 *
 * Cada ítem del listado incluye:
 * - Asignatura del apunte.
 * - Fecha del apunte (en formato "yyyy-MM-dd").
 * - Contenido del apunte.
 *
 * Se permite manejar acciones de:
 * - Click corto (`onClick`) para visualizar o editar.
 * - Click largo (`onLongClick`) para mostrar un menú de opciones.
 *
 * @param lista Lista de objetos Apunte que se mostrarán.
 * @param onClick Acción al hacer click sobre un apunte.
 * @param onLongClick Acción al hacer long click sobre un apunte.
 */
class ApunteAdapter(
    private val lista: List<Apunte>,
    private val onClick: (Apunte) -> Unit,
    private val onLongClick: (Apunte) -> Unit
) : RecyclerView.Adapter<ApunteAdapter.ApunteViewHolder>() {

    class ApunteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAsignatura: TextView = itemView.findViewById(R.id.tvAsignatura)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvContenido: TextView = itemView.findViewById(R.id.tvContenido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApunteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_apunte, parent, false)
        return ApunteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApunteViewHolder, position: Int) {
        val apunte = lista[position]
        holder.tvAsignatura.text = apunte.asignatura.ifEmpty { "Sin asignatura" }
        holder.tvFecha.text = "📅 ${apunte.fecha.ifEmpty { "Sin fecha" }}"
        holder.tvContenido.text = apunte.contenido

        holder.itemView.setOnClickListener {
            onClick(apunte)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(apunte)
            true
        }
    }

    override fun getItemCount(): Int = lista.size
}

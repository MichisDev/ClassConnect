package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadigital.R
import model.Evento


/**
 * Adaptador para mostrar eventos en un diálogo dentro del calendario.
 * Cada ítem muestra el título del evento y una hora, acompañado de un icono
 * que representa el tipo de evento ("comun" o "personal").
 *
 * Se utiliza en CalendarioActivity al pulsar sobre un día con eventos.
 */
class EventoDialogAdapter(
    private val eventos: List<Evento>
) : RecyclerView.Adapter<EventoDialogAdapter.EventoViewHolder>() {

    /**
     * ViewHolder que representa cada evento individual dentro del diálogo.
     * Incluye un icono representativo y un título con hora.
     */
    inner class EventoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icono: ImageView = view.findViewById(R.id.ivIcono)
        val titulo: TextView = view.findViewById(R.id.tvTituloEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento_dialogo, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.titulo.text = "${evento.titulo} (${evento.hora})"
        // Asignar icono según el tipo de evento
        holder.icono.setImageResource(
            when (evento.tipo) {
                "comun" -> R.drawable.ic_evento_naranja
                "personal" -> R.drawable.ic_evento_azul
                else -> R.drawable.ic_evento_azul
            }
        )
    }

    override fun getItemCount(): Int = eventos.size
}
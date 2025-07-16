package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadigital.R
import com.example.agendadigital.databinding.ItemEventoBinding
import model.Evento

/**
 * Adaptador para mostrar eventos en un RecyclerView.
 * Permite mostrar eventos en modo lectura o con opciones de edición y eliminación.
 */
class EventosAdapter(
    private var eventos: List<Evento>,
    private val onEliminar: (Evento) -> Unit,
    private val onEditar: (Evento) -> Unit,
    private val soloLectura: Boolean = false // Si es true, oculta las opciones de editar/eliminar
) : RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {

    /**
     * ViewHolder que representa cada elemento (evento) en la lista.
     */
    inner class EventoViewHolder(val binding: ItemEventoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Enlaza los datos del evento con la vista.
         */
        fun bind(evento: Evento) {
            binding.tvTitulo.text = evento.titulo
            binding.tvDescripcion.text = evento.descripcion
            binding.tvFecha.text = "${evento.fecha} ${evento.hora}"

            // Configura el color y el texto del tipo de evento
            binding.tvTipoEvento.text = if (evento.tipo == "comun") "Evento común" else "Evento personal"

            val context = binding.root.context

            if (evento.tipo == "comun") {
                binding.tvTipoEvento.apply {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_primary))
                    setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimary))
                    text = "Evento común"
                }
            } else {
                binding.tvTipoEvento.apply {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_primaryContainer))
                    setTextColor(ContextCompat.getColor(context, R.color.md_theme_onTertiary))
                    text = "Evento personal"
                }
            }

            // Configura visibilidad de opciones según el modo de soloLectura
            binding.btnEditar.visibility = if (soloLectura) View.GONE else View.VISIBLE
            binding.btnEliminar.visibility = if (soloLectura) View.GONE else View.VISIBLE

            // Acciones de los botones (solo si no es soloLectura)
            binding.btnEditar.setOnClickListener {
                if (!soloLectura) onEditar(evento)
            }

            binding.btnEliminar.setOnClickListener {
                if (!soloLectura) onEliminar(evento)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val binding = ItemEventoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        holder.bind(eventos[position])
    }

    override fun getItemCount() = eventos.size

    /**
     * Actualiza la lista de eventos mostrados.
     */
    fun actualizarLista(nueva: List<Evento>) {
        eventos = nueva
        notifyDataSetChanged()
    }
}

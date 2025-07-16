package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadigital.databinding.ItemSolicitudBinding
import model.SolicitudUsuPadre

/**
 * Adapter para mostrar una lista de solicitudes de padres.
 * Permite aceptar o rechazar cada solicitud mediante callbacks.
 */
class SolicitudesAdapter(
    /** Función callback que se ejecuta al aceptar una solicitud */
    val onAceptar: (solicitud: SolicitudUsuPadre) -> Unit,
    /** Función callback que se ejecuta al rechazar una solicitud */
    val onRechazar: (id: String) -> Unit
) : RecyclerView.Adapter<SolicitudesAdapter.ViewHolder>() {
    /** Lista interna de solicitudes */
    private var lista = listOf<SolicitudUsuPadre>()

    /**
     * Actualiza la lista de solicitudes mostradas
     * @param nueva Lista nueva de solicitudes
     */
    fun submitList(nueva: List<SolicitudUsuPadre>) {
        lista = nueva
        notifyDataSetChanged()
    }

    /** ViewHolder para enlazar cada ítem con sus datos */
    inner class ViewHolder(val binding: ItemSolicitudBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Enlaza los datos de una solicitud con la vista
         * @param s Objeto SolicitudUsuPadre con la información
         */
        fun bind(s: SolicitudUsuPadre) {
            binding.tvNombre.text = s.nombre
            binding.tvEmail.text = s.email
            binding.btnAceptar.setOnClickListener {
                onAceptar(s)
            }
            binding.btnRechazar.setOnClickListener {
                onRechazar(s.id)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSolicitudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = lista.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }
}
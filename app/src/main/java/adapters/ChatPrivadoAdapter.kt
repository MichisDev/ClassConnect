package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadigital.databinding.ItemMensajeOtroBinding
import com.example.agendadigital.databinding.ItemMensajePropioBinding
import model.MensajePrivado
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptador para mostrar mensajes privados en un RecyclerView.
 * Distingue entre mensajes enviados por el usuario actual (propios)
 * y los recibidos de otros usuarios (ajenos).
 *
 * @param lista Lista de mensajes a mostrar.
 * @param miUid UID del usuario actual para diferenciar mensajes propios.
 */
class ChatPrivadoAdapter(
    private val lista: List<MensajePrivado>,
    private val miUid: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TIPO_PROPIO = 1
        private const val TIPO_OTRO = 2
    }

    /**
     * Determina el tipo de vista según si el mensaje es propio o ajeno.
     */
    override fun getItemViewType(position: Int): Int {
        val tipo = if (lista[position].autorId == miUid) TIPO_PROPIO else TIPO_OTRO
        println("📦 Posición $position → tipo: $tipo (${lista[position].autorId})")
        return tipo
    }

    /**
     * Infla la vista correspondiente al tipo de mensaje.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TIPO_PROPIO) {
            val binding = ItemMensajePropioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PropioViewHolder(binding)
        } else {
            val binding = ItemMensajeOtroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OtroViewHolder(binding)
        }
    }

    /**
     * Asocia los datos del mensaje con la vista correspondiente.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = lista[position]
        if (holder is PropioViewHolder) holder.bind(mensaje)
        if (holder is OtroViewHolder) holder.bind(mensaje)
    }

    override fun getItemCount(): Int = lista.size

    /**
     * ViewHolder para mensajes enviados por el usuario actual.
     */
    inner class PropioViewHolder(private val binding: ItemMensajePropioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(m: MensajePrivado) {
            binding.tvAutorPropio.text = "Yo"
            binding.tvMensajePropio.text = m.contenido
            binding.tvHoraPropio.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(m.timestamp))
        }
    }

    /**
     * ViewHolder para mensajes recibidos de otros usuarios.
     */
    inner class OtroViewHolder(private val binding: ItemMensajeOtroBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(m: MensajePrivado) {
            binding.tvAutorOtro.text = m.autorNombre
            binding.tvMensajeOtro.text = m.contenido
            binding.tvHoraOtro.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(m.timestamp))
        }
    }
}

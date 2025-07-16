package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadigital.databinding.ItemUsuarioChatBinding
import model.UsuarioChat

/**
 * Adaptador para mostrar una lista de usuarios disponibles para iniciar un chat privado.
 *
 * Este adaptador se utiliza dentro de la actividad `ConversacionesPrivadasActivity`
 * y permite al usuario seleccionar un contacto con el cual iniciar una conversación.
 *
 * @param usuarios Lista de usuarios con los que se puede iniciar un chat.
 * @param onClick Función lambda que se ejecuta al hacer clic en un usuario (para iniciar chat).
 */
class UsuarioChatAdapter(
    private val usuarios: List<UsuarioChat>,
    private val onClick: (UsuarioChat) -> Unit
) : RecyclerView.Adapter<UsuarioChatAdapter.UsuarioViewHolder>() {

    /**
     * ViewHolder que representa visualmente a un usuario dentro del RecyclerView.
     *
     * @param binding Enlace con el layout XML `item_usuario_chat.xml`.
     */
    inner class UsuarioViewHolder(val binding: ItemUsuarioChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return UsuarioViewHolder(binding)
    }

    override fun getItemCount(): Int = usuarios.size

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.binding.tvNombreUsuario.text = usuario.nombre
        holder.itemView.setOnClickListener { onClick(usuario) }
    }
}

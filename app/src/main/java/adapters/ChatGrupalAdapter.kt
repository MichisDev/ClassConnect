package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import model.MensajeGrupal
import com.example.agendadigital.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptador para mostrar mensajes del chat grupal entre padres y tutor.
 *
 * @param mensajes Lista de mensajes a mostrar.
 */
class ChatGrupalAdapter(
    private val mensajes: List<MensajeGrupal>
) : RecyclerView.Adapter<ChatGrupalAdapter.MensajeViewHolder>() {

    // ViewHolder adaptado a ambos tipos de mensaje
    inner class MensajeViewHolder(view: View, val esEnviado: Boolean) :
        RecyclerView.ViewHolder(view) {
        val tvMensaje: TextView = view.findViewById(R.id.tvMensajeGrupal)
        val tvHora: TextView = view.findViewById(R.id.tvHoraGrupal)
        val tvAutor: TextView? = if (!esEnviado) view.findViewById(R.id.tvAutorGrupal) else null
    }

    override fun getItemViewType(position: Int): Int {
        val mensaje = mensajes[position]
        val uidActual = FirebaseAuth.getInstance().currentUser?.uid
        return if (mensaje.autorId == uidActual) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val layoutId = if (viewType == 1)
            R.layout.item_message_grupal_enviado
        else
            R.layout.item_message_grupal_recibido

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MensajeViewHolder(view, esEnviado = (viewType == 1))
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.tvMensaje.text = mensaje.contenido

        val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvHora.text = formato.format(Date(mensaje.timestamp))

        holder.tvAutor?.text = mensaje.autor
    }

    override fun getItemCount() = mensajes.size
}

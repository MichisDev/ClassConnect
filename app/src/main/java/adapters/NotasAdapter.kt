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
 * Adaptador para mostrar una lista de objetos {@link Nota} en un RecyclerView.
 *
 * Este adaptador se encarga de inflar las vistas del layout {@code item_nota}
 * y enlazarlas con los datos de cada nota, mostrando asignatura, calificación,
 * fecha de registro y estado de validación.
 *
 * Características:
 * - Las notas validadas se muestran en color verde con el texto "✅ Validada".
 * - Las notas no validadas se muestran en rojo con el texto "⏳ Pendiente de validación".
 * - La fecha se formatea al formato dd/MM/yyyy desde el timestamp.
 *
 * @param lista Lista de objetos {@link Nota} que se mostrará en el RecyclerView.
 */
class NotasAdapter(private val lista: List<Nota>) :
    RecyclerView.Adapter<NotasAdapter.NotaViewHolder>() {

    class NotaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvAsignatura: TextView = view.findViewById(R.id.tvAsignatura)
        val tvNota: TextView = view.findViewById(R.id.tvNota)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaNota)
        val tvEstadoNota: TextView = view.findViewById(R.id.tvEstadoNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = lista[position]
        holder.tvTitulo.text = nota.titulo
        holder.tvAsignatura.text = "Asignatura: ${nota.asignatura}"
        holder.tvNota.text = "Nota: ${nota.calificacion}"
        holder.tvFecha.text = "📅 ${nota.fecha}"  // NOTA: usamos el campo 'fecha' ahora

        holder.tvEstadoNota.text = if (nota.validada) "✅ Validada" else "⏳ Pendiente de validación"
        holder.tvEstadoNota.setTextColor(
            if (nota.validada)
                Color.parseColor("#4CAF50") // verde
            else
                Color.parseColor("#F44336") // rojo
        )

    }

    override fun getItemCount() = lista.size
}

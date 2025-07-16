package repository

import com.google.firebase.firestore.FirebaseFirestore
import model.Evento

class EventosRepository {

    private val db = FirebaseFirestore.getInstance()
    private val eventosRef = db.collection("eventos")

    /**
     * Obtiene todos los eventos ordenados por timestamp.
     * @param onSuccess función callback con la lista de eventos obtenida.
     * @param onFailure función callback que se ejecuta si hay error.
     */
    fun cargarEventos(onSuccess: (List<Evento>) -> Unit, onFailure: () -> Unit) {
        eventosRef.get() // 🔁 Quitamos orderBy("timestamp")
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Evento::class.java)?.copy(id = doc.id)
                }
                onSuccess(lista)
            }
            .addOnFailureListener {
                onFailure()
            }
    }


    /**
     * Crea un nuevo evento en Firestore.
     */
    fun crearEvento(evento: Evento, onSuccess: () -> Unit, onFailure: () -> Unit) {
        eventosRef.add(evento)
            .addOnSuccessListener { docRef ->
                val eventoConId = evento.copy(id = docRef.id)
                eventosRef.document(docRef.id).set(eventoConId) // 🔁 Ahora guarda con ID
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure() }
            }
            .addOnFailureListener { onFailure() }
    }


    /**
     * Actualiza un evento existente en Firestore.
     */
    fun actualizarEvento(evento: Evento, onSuccess: () -> Unit, onFailure: () -> Unit) {
        eventosRef.document(evento.id).set(evento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    /**
     * Elimina un evento por su ID.
     */
    fun eliminarEvento(eventoId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        eventosRef.document(eventoId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}

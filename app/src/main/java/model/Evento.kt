package model

/**
 * Modelo de datos que representa un evento dentro de la aplicación.
 *
 * @property id Identificador único del evento (generado por Firestore).
 * @property titulo Título del evento.
 * @property descripcion Descripción opcional del evento.
 * @property fecha Fecha del evento en formato "yyyy-MM-dd".
 * @property hora Hora del evento en formato "HH:mm".
 * @property tipo Tipo de evento: puede ser "personal" o "comun".
 * @property creadorUid UID del usuario que creó el evento.
 * @property aulaId Identificador del aula asociada al evento (relevante para eventos comunes).
 */
data class Evento(
    val id: String = "",                      // ID del evento
    val titulo: String = "",                  // Título del evento
    val descripcion: String = "",             // Descripción opcional
    val fecha: String = "",                   // Formato: "yyyy-MM-dd"
    val hora: String = "",                    // Formato: "HH:mm"
    val tipo: String = "personal",            // "personal" o "comun"
    val creadorUid: String = "",              // UID del usuario que creó el evento
    val aulaId: String = "",                  // Aula asociada (si es común)
)

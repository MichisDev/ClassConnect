package model

/**
 * Modelo de datos que representa un mensaje en el chat grupal.
 *
 * @property contenido Texto del mensaje enviado.
 * @property autor Nombre del usuario que envió el mensaje.
 * @property timestamp Momento exacto del envío del mensaje (en milisegundos desde epoch).
 */
data class MensajeGrupal(
    val contenido: String = "",
    val autor: String = "",
    val autorId: String = "",
    val timestamp: Long = 0L
)

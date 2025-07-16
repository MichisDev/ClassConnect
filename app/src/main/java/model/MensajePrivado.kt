package model

/**
 * Modelo de datos que representa un mensaje enviado en una conversación privada.
 *
 * @property contenido Texto del mensaje.
 * @property autorId UID del usuario que envió el mensaje.
 * @property receptorId UID del usuario que recibe el mensaje.
 * @property timestamp Momento en milisegundos en que fue enviado el mensaje.
 * @property autorNombre Nombre visible del autor del mensaje.
 * @property conversacionId Identificador único de la conversación (formado por los dos UID en orden alfabético).
 */
data class MensajePrivado(
    val contenido: String = "",
    val autorId: String = "",
    val receptorId: String = "",
    val timestamp: Long = 0L,
    val autorNombre: String = "",
    val conversacionId: String = ""
)

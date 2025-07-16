package model

/**
 * Modelo de datos que representa a un usuario disponible para iniciar una conversación privada.
 *
 * @property uid Identificador único del usuario en Firebase Authentication.
 * @property nombre Nombre visible del usuario.
 */
data class UsuarioChat(
    val uid: String = "",
    val nombre: String = ""
)

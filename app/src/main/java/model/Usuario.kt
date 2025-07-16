package model

/**
 * Modelo de datos que representa a un usuario registrado en la aplicación.
 *
 * Propiedades:
 * @property uid ID único del usuario, proporcionado por Firebase Authentication.
 * @property nombre Nombre completo del usuario.
 * @property password Contraseña del usuario (se recomienda en producción no guardarla en texto plano).
 * @property email Correo electrónico asociado al usuario.
 * @property rol Rol asignado al usuario en la aplicación (puede ser "profesor", "padre" o "alumno").
 *
 * Uso:
 * - Esta clase se utiliza para registrar y recuperar la información de usuarios desde Firestore.
 * - Es útil en la autenticación y gestión de permisos según el rol del usuario.
 */
data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val password: String = "",
    val email: String = "",
    val rol: String = ""
)

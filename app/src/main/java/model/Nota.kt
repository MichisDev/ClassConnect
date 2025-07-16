package model

/**
 * Clase de datos que representa una nota o calificación académica de un alumno.
 *
 * @property id Identificador único del documento de nota en Firestore.
 * @property titulo Título del examen o actividad evaluada.
 * @property alumno Nombre del alumno al que pertenece la nota.
 * @property asignatura Nombre de la asignatura a la que pertenece la nota.
 * @property calificacion Calificación numérica obtenida, en formato Double.
 * @property fecha Fecha del examen o actividad evaluada (formato: yyyy-MM-dd).
 * @property timestamp Fecha y hora de creación del registro en milisegundos.
 * @property validada Indica si la nota ha sido validada por un profesor.
 */
data class Nota(
    val id: String = "",
    val titulo: String = "",
    val alumno: String = "",
    val asignatura: String = "",
    val calificacion: Double = 0.0,
    val fecha: String = "",
    val timestamp: Long = 0L,
    val validada: Boolean = false
)

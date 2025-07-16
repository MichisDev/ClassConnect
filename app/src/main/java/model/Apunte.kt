package model

/**
 * Clase de datos que representa un apunte personal del alumno.
 *
 * Un apunte puede estar asociado a una asignatura concreta y tener una fecha específica de creación,
 * además de un contenido textual libre. Se almacena junto a un identificador único y un timestamp.
 *
 * @property id Identificador único del apunte (generado automáticamente por Firestore).
 * @property contenido Texto libre introducido por el alumno.
 * @property timestamp Marca de tiempo en milisegundos, útil para ordenar cronológicamente.
 * @property asignatura Nombre de la asignatura relacionada con el apunte (ej. "Matemáticas").
 * @property fecha Fecha en formato "yyyy-MM-dd" cuando fue tomado el apunte (seleccionada por el usuario).
 */
data class Apunte(
    val id: String = "",
    val contenido: String = "",
    val timestamp: Long = 0L,
    val asignatura: String = "",
    val fecha: String = "" // Formato: yyyy-MM-dd
)


package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Evento
import repository.EventosRepository

class EventosViewModel : ViewModel() {

    private val repository = EventosRepository()

    private val _eventos = MutableLiveData<List<Evento>>()
    val eventos: LiveData<List<Evento>> get() = _eventos

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    private var usuarioUid: String = ""
    private var esProfesor: Boolean = false

    /**
     * Establece el UID del usuario y si es profesor o no.
     */
    fun setUsuario(uid: String, profesor: Boolean) {
        usuarioUid = uid
        esProfesor = profesor
    }

    /**
     * Solicita los eventos al repositorio y actualiza LiveData.
     */
    fun cargarEventos() {
        repository.cargarEventos(
            onSuccess = { lista ->
                _eventos.value = lista.filter { evento ->
                    if (evento.tipo == "comun") true
                    else evento.creadorUid == usuarioUid
                }.sortedBy { it.fecha }
                _error.value = false
            },
            onFailure = {
                _error.value = true
            }
        )
    }


    /**
     * Envía un nuevo evento a la base de datos.
     */
    fun crearEvento(evento: Evento) {
        repository.crearEvento(
            evento,
            onSuccess = { cargarEventos() },
            onFailure = { _error.value = true }
        )
    }


    /**
     * Actualiza un evento existente.
     */
    fun editarEvento(evento: Evento) {
        if (evento.creadorUid == usuarioUid || esProfesor) {
            repository.actualizarEvento(
                evento,
                onSuccess = { cargarEventos() },
                onFailure = { _error.value = true }
            )
        }
    }

    /**
     * Elimina un evento.
     */
    fun eliminarEvento(id: String) {
        repository.eliminarEvento(
            id,
            onSuccess = { cargarEventos() },
            onFailure = { _error.value = true }
        )
    }
}

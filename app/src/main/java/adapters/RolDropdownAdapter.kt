package com.example.agendadigital.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.agendadigital.R

/**
 * Adaptador personalizado para mostrar roles de usuario con íconos en un Spinner desplegable.
 *
 * Este adaptador se utiliza principalmente en la pantalla de registro/login
 * para permitir que el usuario seleccione su rol (profesor, padre, alumno),
 * mostrando un ícono representativo junto al nombre del rol.
 *
 * El layout asociado debe tener un TextView (`@id/tvRol`) y un ImageView (`@id/ivIcono`).
 *
 * @param context Contexto de la aplicación.
 * @param roles Lista de nombres de roles a mostrar en el desplegable.
 *
 * Ejemplo de uso:
 * ```
 * val roles = listOf("Profesor", "Padre", "Alumno")
 * val adapter = RolDropdownAdapter(this, roles)
 * spinner.adapter = adapter
 * ```
 */
class RolDropdownAdapter(context: Context, private val roles: List<String>) :
    ArrayAdapter<String>(context, 0, roles) {

    /**
     * Método que define la vista que se muestra en el Spinner cuando no está desplegado.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    /**
     * Método que define la vista para cada elemento dentro del desplegable.
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    /**
     * Crea una vista personalizada para cada rol, asignando texto e icono correspondiente.
     *
     * @param position Índice del elemento actual.
     * @param convertView Vista reciclada si existe.
     * @param parent Vista padre del componente.
     * @return Vista inflada con datos del rol.
     */
    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_rol_dropdown, parent, false)

        val rolText = view.findViewById<TextView>(R.id.tvRol)
        val rolIcon = view.findViewById<ImageView>(R.id.ivIcono)

        val rol = roles[position]
        rolText.text = rol

        // Asigna un icono basado en el nombre del rol (en minúsculas)
        val iconRes = when (rol.lowercase()) {
            "profesor" -> R.drawable.ic_profesor
            "padre" -> R.drawable.ic_familia
            "alumno" -> R.drawable.ic_alumno
            else -> R.drawable.ic_agenda
        }
        rolIcon.setImageResource(iconRes)

        return view
    }
}

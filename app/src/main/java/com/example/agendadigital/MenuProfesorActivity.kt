package com.example.agendadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agendadigital.databinding.ActivityMenuProfesorBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Actividad principal del menú para el rol Profesor.
 * Desde aquí se puede acceder a las funciones clave como revisión de solicitudes,
 * gestión de eventos, chats, calendario y validación de notas.
 */
class MenuProfesorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuProfesorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuProfesorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Navega a la pantalla de revisión de solicitudes de padres.
         */
        binding.cardSolicitudes.setOnClickListener {
            startActivity(Intent(this, GestionAulaActivity::class.java))
        }

        /**
         * Navega a la gestión de eventos (crear/ver).
         */
        binding.cardEventos.setOnClickListener {
            startActivity(Intent(this, EventosActivity::class.java))
        }

        /**
         * Abre el chat grupal con los padres.
         */
        binding.cardChatGrupal.setOnClickListener {
            startActivity(Intent(this, ChatGrupalActivity::class.java))
        }

        /**
         * Inicia la actividad para seleccionar un padre y abrir un chat privado.
         * Primero se comprueba que haya al menos un padre registrado.
         */
        binding.cardChatPrivado.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .whereEqualTo("rol", "padre")
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    val padre = result.documents.firstOrNull()
                    val uidPadre = padre?.getString("uid")
                    val nombrePadre = padre?.getString("nombre")
                    if (uidPadre != null && nombrePadre != null) {
                        val intent = Intent(this, SeleccionarUsuarioActivity::class.java)
                        intent.putExtra("rolContrario", "padre")
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "No se encontró ningún padre", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al buscar padre", Toast.LENGTH_SHORT).show()
                }
        }

        /**
         * Abre el calendario personalizado para el profesor.
         */
        binding.cardCalendario.setOnClickListener {
            val intent = Intent(this, CalendarioActivity::class.java)
            intent.putExtra("tipoUsuario", "profesor")
            startActivity(intent)
        }

        /**
         * Navega a la pantalla para validar notas de los alumnos.
         */
        binding.cardValidarNotas.setOnClickListener {
            startActivity(Intent(this, ValidarNotasActivity::class.java))
        }
    }
}

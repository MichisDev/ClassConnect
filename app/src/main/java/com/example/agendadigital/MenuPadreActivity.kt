package com.example.agendadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agendadigital.databinding.ActivityMenuPadreBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity que representa el menú principal para usuarios con rol "padre".
 *
 * Desde este menú, el padre puede:
 * - Acceder al chat privado con un tutor (profesor).
 * - Ver eventos asociados.
 * - Acceder al chat grupal con profesores y otros padres.
 * - Consultar el calendario escolar.
 *
 * Esta clase utiliza Firebase Firestore para localizar un tutor asignado
 * y redirige a otras actividades según la opción elegida.
 */

class MenuPadreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPadreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPadreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ir a ver eventos
        binding.cardVerEventos.setOnClickListener {
            val intent = Intent(this, EventosPadresActivity::class.java)
            startActivity(intent)
        }


        // Ir al chat padre-tutor individual
        binding.cardChatPrivado.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .whereEqualTo("rol", "profesor")
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    val profe = result.documents.firstOrNull()
                    val uidTutor = profe?.getString("uid")
                    val nombreTutor = profe?.getString("nombre")
                    if (uidTutor != null && nombreTutor != null) {
                        val intent = Intent(this, SeleccionarUsuarioActivity::class.java)
                        intent.putExtra("rolContrario", "profesor")
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "No se encontró ningún tutor", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al buscar tutor", Toast.LENGTH_SHORT).show()
                }
        }



        // NUEVO: Ir al chat grupal
        binding.cardChatGrupal.setOnClickListener {
            val intent = Intent(this, ChatGrupalActivity::class.java)
            startActivity(intent)
        }

        binding.cardCalendario.setOnClickListener {
            val intent = Intent(this, CalendarioActivity::class.java)
            intent.putExtra("tipoUsuario", "padre")
            startActivity(intent)
        }

    }
}

package com.example.agendadigital

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agendadigital.databinding.ActivityParentRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity que permite a los usuarios registrarse como padres mediante una solicitud validada por código.
 *
 * Funcionalidades principales:
 * - El usuario introduce su nombre, correo, contraseña y un código de validación.
 * - Se verifica el código en Firestore dentro de la colección "codigos".
 * - Si el código es válido y corresponde a un rol "padre", se guarda una solicitud en la colección "solicitudes".
 * - El estado inicial de la solicitud es "pendiente" y se espera validación manual posterior.
 *
 * Componentes utilizados:
 * - {@link ActivityParentRegisterBinding} para acceder a los elementos de la interfaz.
 * - {@link FirebaseFirestore} para verificar el código y guardar la solicitud.
 * - {@link Toast} para mostrar mensajes de éxito o error al usuario.
 *
 * Requisitos:
 * - El código debe estar registrado en la colección "codigos" y tener el campo "tipo" con valor "padre".
 * - Todos los campos del formulario deben estar completos para procesar la solicitud.
 *
 * Flujo:
 * 1. El usuario rellena los campos y pulsa el botón de solicitar.
 * 2. Se valida el código introducido.
 * 3. Si es válido, se guarda la solicitud como documento Firestore con los campos introducidos.
 */
class ParentRegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityParentRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityParentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón de solicitar
        binding.solicitarButton.setOnClickListener {
            val nombre = binding.nombreEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val codigo = binding.codigoEditText.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || codigo.isEmpty()) {
                Toast.makeText(this, getString(R.string.mensaje_campos_vacios), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.mensaje_email_invalido), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, getString(R.string.mensaje_contrasena_corta), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            verificarCodigoYGuardarSolicitud(nombre, email, password, codigo)
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /**
     * Verifica el código proporcionado y, si es válido, guarda una solicitud en Firestore.
     */
    private fun verificarCodigoYGuardarSolicitud(
        nombre: String,
        email: String,
        password: String,
        codigo: String
    ) {
        db.collection("codigos").document(codigo).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.getString("tipo") == "padre") {
                    val solicitud = hashMapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "password" to password,
                        "codigo" to codigo,
                        "estado" to "pendiente",
                        "rol" to "padre"
                    )
                    db.collection("solicitudes")
                        .add(solicitud)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al enviar la solicitud", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Código inválido o no válido para padres", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al verificar el código", Toast.LENGTH_SHORT).show()
            }
    }
}
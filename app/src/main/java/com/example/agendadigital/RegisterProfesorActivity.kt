package com.example.agendadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agendadigital.databinding.ActivityProfesorRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.Usuario

/**
 * Activity que gestiona el registro de un nuevo profesor en la aplicación.
 *
 * Funcionalidades principales:
 * - Permite al profesor introducir sus datos personales: nombre, email y contraseña.
 * - Verifica que las contraseñas coincidan y que los campos estén completos.
 * - Registra al profesor en Firebase Authentication y guarda su información en Firestore.
 * - En caso de éxito, redirige al usuario al MainActivity.
 *
 * Componentes usados:
 * - {@link FirebaseAuth} para la autenticación de usuarios.
 * - {@link FirebaseFirestore} para almacenar el objeto {@link Usuario} del profesor.
 * - {@link ActivityProfesorRegisterBinding} para acceso a los elementos de la vista con ViewBinding.
 *
 * Notas:
 * - La contraseña se guarda en texto plano dentro del objeto Usuario solo como representación.
 *   Para entornos reales se recomienda evitar esto por razones de seguridad.
 */
class RegisterProfesorActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityProfesorRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfesorRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()


        binding.registerButton.setOnClickListener {
            val nombre = binding.nombreEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val passwordRe = binding.passwordEditTextRe.text.toString().trim()

            // Validaciones
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || passwordRe.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validar formato de correo electrónico
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            if (!emailRegex.matches(email)) {
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar longitud de contraseña
            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // Validar que las contraseñas coincidan
            if (password != passwordRe) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            val uid = it.uid
                            val usuario = Usuario(uid, nombre, password, email, "profesor")
                            db.collection("usuarios").document(uid)
                                .set(usuario)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT)
                                        .show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Error al guardar usuario",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Error en el registro: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
